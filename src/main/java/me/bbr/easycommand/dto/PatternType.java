package me.bbr.easycommand.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.join;

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

    public String getMainPattern() {
        return mainPattern;
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

    public static PatternType aPatternType(Class clazz, String mainPattern, String... synonymPatterns) {
        return new PatternType(clazz, mainPattern, Arrays.<String>asList((String[]) synonymPatterns));
    }

    public List<String> getSynonymPatterns() {
        return synonymPatterns;
    }

    public String getFormattedPatterns() {
        return  join("|", patterns.stream()
                                .map(this::betweenParenthesis)
                                .toArray(size -> new String [size]));


    }

    private String betweenParenthesis(String mainPattern) {
        return "(" + addEscapeChars(mainPattern) + ")";
    }

    private String addEscapeChars(String mainPattern) {
        return mainPattern
                    .replaceAll("\\\\","\\\\\\\\")
                    .replaceAll("\\.\\+", "\\\\.\\+")
                    .replaceAll("\\+", "\\\\+")
                    .replaceAll("\\.\\*", "\\\\.\\\\*")
                    .replaceAll("\\(", "\\\\(")
                    .replaceAll("\\)", "\\\\)")
                    .replaceAll("\\\\\\.", "\\\\.")
                    .replaceAll("\\|", "\\\\|")
                ;
    }

    public List<String>  getAllPattern() {
        return patterns;
    }
}
