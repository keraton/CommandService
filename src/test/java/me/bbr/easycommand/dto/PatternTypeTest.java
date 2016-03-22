package me.bbr.easycommand.dto;

import org.junit.Test;

import java.util.regex.Pattern;

import static me.bbr.easycommand.dto.PatternType.aPatternType;
import static me.bbr.easycommand.dto.PatternTypeTest.P.a;
import static org.assertj.core.api.Assertions.assertThat;

public class PatternTypeTest {

    @Test
    public void should_create_patternOfPattern () {
        assertPattern(a("(\\w+)"), "(\\(\\\\w\\+\\))");
        assertPattern(a("(\\w+)", "(STRING)"), "(\\(\\\\w\\+\\))|(\\(STRING\\))");
        assertPattern(a("(\\d+)"), "(\\(\\\\d\\+\\))");
        assertPattern(a("(\\d+)", "(INT)"), "(\\(\\\\d\\+\\))|(\\(INT\\))");
        assertPattern(a("(\\d+\\.\\d+)"), "(\\(\\\\d\\+\\\\.\\\\d\\+\\))");
        assertPattern(a("(\\d+)", "(INT)"), "(\\(\\\\d\\+\\))|(\\(INT\\))");
        assertPattern(a("(.*)"), "(\\(\\.\\*\\))");
        assertPattern(a("(.+)"), "(\\(\\.\\+\\))");
        assertPattern(a("(TRUE|FALSE|true|false)", "(BOOLEAN)"),
                            "(\\(TRUE\\|FALSE\\|true\\|false\\))|(\\(BOOLEAN\\))");

    }

    private void assertPattern(P p, String expected) {
        assertThat(aPatternType(null, p.main, p.synonym).getFormattedPatterns()).isEqualTo(expected);
    }

    static class P {

        private String main;

        private String[] synonym;

        public P(String main, String[] synonym) {
            this.main = main;
            this.synonym = synonym;
        }

        static P a(String main, String... synonym) {
            return new P(main, synonym);
        }

    }

}
