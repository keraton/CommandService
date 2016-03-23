package me.bbr.easycommand.dto;

import org.junit.Test;

import static me.bbr.easycommand.dto.PatternType.aPatternType;
import static org.assertj.core.api.Assertions.assertThat;

public class PatternTypeTest {

    @Test
    public void should_create_patternOfPattern () {
        PatternType patternType = aPatternType(Integer.TYPE, "Original", "Synonym");
        assertThat(patternType.replaceSynonymWithMain("Synonym")).isEqualTo("Original");
    }



}
