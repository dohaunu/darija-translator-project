package com.aui.translator.api;

import com.aui.translator.service.GeminiTranslationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.Map;

@Path("/translate")
@Produces(MediaType.APPLICATION_JSON)
public class TranslatorResource {
    @Inject GeminiTranslationService translator;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response translate(TranslateRequest request) {
        if (request == null || request.text == null || request.text.isBlank())
            return Response.status(400).entity(Map.of("error", "text must not be empty")).build();
        if (request.text.length() > 5000)
            return Response.status(413).entity(Map.of("error", "text exceeds 5000 characters")).build();
        String language = request.sourceLanguage == null || request.sourceLanguage.isBlank() ? "English" : request.sourceLanguage.trim();
        if (language.length() > 40) return Response.status(400).entity(Map.of("error", "invalid source language")).build();
        try {
            return Response.ok(new TranslateResponse(language, request.text, translator.translate(request.text, language))).build();
        } catch (IllegalStateException ex) {
            return Response.status(502).entity(Map.of("error", "Translation provider unavailable or not configured")).build();
        } catch (Exception ex) {
            return Response.status(502).entity(Map.of("error", "Translation request failed")).build();
        }
    }

    @OPTIONS
    public Response options() { return Response.noContent().build(); }
}
