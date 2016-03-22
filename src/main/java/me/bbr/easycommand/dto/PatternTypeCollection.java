package me.bbr.easycommand.dto;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.join;
import static java.util.Arrays.asList;
import static me.bbr.easycommand.dto.PatternType.aPatternType;

public class PatternTypeCollection {


    private static PatternType [] patternCollection = {
            aPatternType(String.class, "(\\w+)","(STRING)"),
            aPatternType(Integer.class, "(\\d+)","(INT)"),
            aPatternType(Double.class, "(\\d+\\.\\d+)" ,"(DOUBLE)"),
            aPatternType(Boolean.class, "(TRUE|FALSE|true|false)" ,"(BOOLEAN)"  ),
    };

    private static List<PatternType> patternTypeList = Arrays.asList(patternCollection);

    private static String allPattern = join("|",asList(patternCollection).stream()
                                    .map(patternType -> patternType.getFormattedPatterns())
                                    .toArray(size -> new String[size]));

    public static String getAllPattern() {
        return allPattern;
    }

    public static List<PatternType> getPatternList() {
        return patternTypeList;
    }

    public static String replaceWithMainPattern(String text) {
        String replaceText = text;
        for (PatternType patternType : patternCollection) {
            replaceText = patternType.replaceSynonymWithMain(replaceText);
        }
        return replaceText;
    }

}
