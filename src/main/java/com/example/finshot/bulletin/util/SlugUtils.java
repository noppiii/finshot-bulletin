package com.example.finshot.bulletin.util;

import java.text.Normalizer;

public class SlugUtils {

    public static String generateSlug(String... parts) {
        String combinedString = String.join(" ", parts);
        String slug = normalizeText(combinedString);

        return slug;
    }

    private static String normalizeText(String text) {
        String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
        normalizedText = normalizedText.replaceAll("[^\\p{ASCII}]", "");
        normalizedText = normalizedText.toLowerCase().replaceAll("\\s+", "-");

        return normalizedText;
    }
}