package com.aui.translator.api;

import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    @Override public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        String origin = request.getHeaderString("Origin");
        String allowed = System.getenv("TRANSLATOR_ALLOWED_ORIGIN");
        // Only explicitly configured origin gets CORS access. Chrome extension can call with host permission.
        if (allowed != null && allowed.equals(origin)) {
            response.getHeaders().putSingle("Access-Control-Allow-Origin", origin);
            response.getHeaders().putSingle("Vary", "Origin");
            response.getHeaders().putSingle("Access-Control-Allow-Methods", "POST, OPTIONS");
            response.getHeaders().putSingle("Access-Control-Allow-Headers", "Authorization, Content-Type");
        }
    }
}
