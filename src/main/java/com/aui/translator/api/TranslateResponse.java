package com.aui.translator.api;

public class TranslateResponse {
    public String sourceLanguage;
    public String targetLanguage = "Moroccan Arabic (Darija)";
    public String originalText;
    public String translation;
    public TranslateResponse() { }
    public TranslateResponse(String language, String original, String translation) {
        this.sourceLanguage = language; this.originalText = original; this.translation = translation;
    }
}
