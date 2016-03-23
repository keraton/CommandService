package com.github.keraton.easydsl.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatternType {

    private final String mainPattern;

    private final List<String> synonymPatterns;

    private final Class clazz;

    private final List<String> patterns;

    private PatternType(Class clazz, String mainPattern, List<String> synonymPattern) {
        this.clazz = clazz;
        this.mainPattern = mainPattern;
        this.synonymPatterns = synonymPattern;

        // Pattern
        patterns = new ArrayList<>();
        patterns.add(mainPattern);
        patterns.addAll(synonymPattern);
    }

    public String replaceSynonymWithMain(String text) {
        String returnedPattern = text;
        for (String pattern: synonymPatterns) {
            if (text.contains(pattern)) {
                returnedPattern = returnedPattern.replace(pattern, mainPattern);
            }
        }
        return  returnedPattern;
    }

    public Class getClazz() {
        return clazz;
    }

    public List<String>  getAllPattern() {
        return patterns;
    }

    public static PatternType aPatternType(Class clazz, String mainPattern, String... synonymPatterns) {
        return new PatternType(clazz, mainPattern, Arrays.asList((String[]) synonymPatterns));
    }
}
