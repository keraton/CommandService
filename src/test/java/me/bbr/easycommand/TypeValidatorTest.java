package me.bbr.easycommand;

import me.bbr.easycommand.dto.PatternTypeCollection;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeValidatorTest {

    @Test
    public void should_extract_nothing () {
        // Given
        TypeExtractor typeExtractor = new TypeExtractor();
        String pattern = "^hello $";

        // When
        List<Class> clazz = typeExtractor.extract(pattern);

        // Then
        assertThat(clazz).isEmpty();
    }

    @Test
    public void should_extract_string () {
        // Given
        TypeExtractor typeExtractor = new TypeExtractor();
        String pattern = "^hello (\\w+)$";

        // When
        List<Class> clazz = typeExtractor.extract(pattern);

        // Then
        assertThat(clazz).containsExactly(String.class);
    }

    @Test
    public void should_extract_two_string () {
        // Given
        TypeExtractor typeExtractor = new TypeExtractor();
        String pattern = "^hello (\\w+) (.*)$";

        // When
        List<Class> clazz = typeExtractor.extract(pattern);

        // Then
        assertThat(clazz).containsExactly(String.class, String.class);
    }

    @Test
    public void should_extract_integer () {
        // Given
        TypeExtractor typeExtractor = new TypeExtractor();
        String pattern = "^hello (\\d+)$";

        // When
        List<Class> clazz = typeExtractor.extract(pattern);

        // Then
        assertThat(clazz).containsExactly(Integer.class);
    }

    @Test
    public void should_extract_double () {
        // Given
        TypeExtractor typeExtractor = new TypeExtractor();
        String pattern = "^hello (\\d+\\.\\d+)$";

        // When
        List<Class> clazz = typeExtractor.extract(pattern);

        // Then
        assertThat(clazz).containsExactly(Double.class);
    }
}
