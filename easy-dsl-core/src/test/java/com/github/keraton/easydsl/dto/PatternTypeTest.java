package com.github.keraton.easydsl.dto;

import org.junit.Test;

import static com.github.keraton.easydsl.dto.PatternType.aPatternType;
import static org.assertj.core.api.Assertions.assertThat;

public class PatternTypeTest {

    @Test
    public void should_create_patternOfPattern () {
        PatternType patternType = aPatternType(Integer.TYPE, "Original", "Synonym");
        assertThat(patternType.replaceSynonymWithMain("Synonym")).isEqualTo("Original");
    }



}
