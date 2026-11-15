package com.aui.translator.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

/** Validates HTTP Basic credentials for the translation endpoint without JASPIC/Elytron login callbacks. */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class BasicAuthFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext request) {
        if (!"translate".equals(request.getUriInfo().getPath()) ||
            "OPTIONS".equalsIgnoreCase(request.getMethod())) return;

        String expectedUser = System.getenv("TRANSLATOR_USER");
        String expectedPassword = System.getenv("TRANSLATOR_PASSWORD");
        String header = request.getHeaderString(HttpHeaders.AUTHORIZATION);
        boolean valid = false;
        if (expectedUser != null && !expectedUser.isBlank() &&
            expectedPassword != null && !expectedPassword.isBlank() &&
            header != null && header.regionMatches(true, 0, "Basic ", 0, 6)) {
            try {
                String pair = new String(Base64.getDecoder().decode(header.substring(6).trim()), StandardCharsets.UTF_8);
                int colon = pair.indexOf(':');
                valid = colon > 0 && constantTimeEquals(pair.substring(0, colon), expectedUser)
                    && constantTimeEquals(pair.substring(colon + 1), expectedPassword);
            } catch (IllegalArgumentException ignored) { /* malformed credentials */ }
        }
        if (!valid) {
            request.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Darija Translator\"")
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", "Unauthorized"))
                .build());
        }
    }
    private static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
