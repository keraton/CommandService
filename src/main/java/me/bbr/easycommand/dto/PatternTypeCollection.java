package me.bbr.easycommand.dto;

import java.util.Arrays;
import java.util.List;

import static me.bbr.easycommand.dto.PatternType.aPatternType;

public class PatternTypeCollection {


    private static PatternType [] patternCollection = {
            aPatternType(String.class, "(\\w+)","(STRING)"),
            aPatternType(Integer.class, "(\\d+)","(INT)"),
            aPatternType(Double.class, "(\\d+\\.\\d+)" ,"(DOUBLE)"),
            aPatternType(Boolean.class, "(TRUE|FALSE|true|false)" ,"(BOOLEAN)"  ),
    };

    private static List<PatternType> patternTypeList = Arrays.asList(patternCollection);


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
