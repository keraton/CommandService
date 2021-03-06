package com.github.keraton.easydsl;

import com.github.keraton.easydsl.annotation.Context;
import com.github.keraton.easydsl.annotation.DateArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DSLValidatorTest {

    @InjectMocks
    private DSLValidator commandValidator = new DSLValidator();

    @Spy
    private TypeExtractor typeExtractor = new TypeExtractor();

    @Test
    public void should_count_only_one_group () {
        // Then
        assertThat(commandValidator.countGroup("Group one (\\w+)")).isEqualTo(1);
        assertThat(commandValidator.countGroup("Group two (\\w+) (\\w+)")).isEqualTo(2);
    }

    @Test
    public void should_match_only_one_line_pattern () {
        // Should start with ^ and end with $
        assertThat(commandValidator.isValid("^This should work$")).isTrue();
        // Should end with $
        assertThat(commandValidator.isValid("^This should not work")).isFalse();
        // Should start with ^
        assertThat(commandValidator.isValid("This should not work$")).isFalse();
        // Should stay one line
        assertThat(commandValidator.isValid("^This should not \n work$")).isFalse();
    }

    @Test
    public void should_valid_with_strings () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodA", String.class, String.class);

        // When
        boolean valid = commandValidator.isValid(methodA, "^ With two (\\w+) (\\w+) $");

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void should_not_valid_with_bad_compilation () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodA", String.class, String.class);

        // When
        boolean valid = commandValidator.isValid("^ With two (*.) $");

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    public void should_not_valid_with_different_size_of_argument () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodA", String.class, String.class);

        // When
        boolean valid = commandValidator.isValid(methodA, "^ With one (\\w+) $");

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    public void should_valid_with_int () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodB", Integer.class, Integer.class);

        // When
        boolean valid = commandValidator.isValid(methodA, "^ With two (\\d+) (\\d+) $");

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void should_valid_with_one_CommandContext () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodC", DSLContext.class);

        // When
        boolean valid = commandValidator.isValid(methodA, "^This should be enough$");

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void should_not_valid_with_CommandContext_not_in_the_last () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodD", DSLContext.class, String.class);

        // When
        boolean valid = commandValidator.isValid(methodA, "^(\\w+)$");

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    public void should_valid_with_CommandContext_in_the_last () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodF", String.class, DSLContext.class);

        // When
        boolean valid = commandValidator.isValid(methodA, "^(\\w+)$");

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void should_not_valid_with_moreThanOne_CommandContext () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodE", DSLContext.class, DSLContext.class);

        // When
        boolean valid = commandValidator.isValid(methodA, "^ $");

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    public void should_valid_with_string_annotate () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodG", String.class);

        // When
        boolean valid = commandValidator.isValid(methodA, "^ $");

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void should_valid_with_string_annotate_in_the_last_place () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodH", String.class, String.class);

        // When
        boolean valid = commandValidator.isValid(methodA, "^ (\\w+) $");

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void should_not_valid_with_string_annotate_notIn_the_last_place () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodI", String.class, String.class);

        // When
        boolean valid = commandValidator.isValid(methodA, "^ (\\w+) $");

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    public void should_valid_with_double () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodJ", Double.class, Integer.class);

        // When
        boolean valid = commandValidator.isValid(methodA, "^ (\\d+\\.\\d+) (\\d+) $");

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void should_valid_with_date () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodK", Date.class);

        // When
        boolean valid = commandValidator.isValid(methodA, "^ (.+) $");

        // Then
        assertThat(valid).isTrue();
    }

    // This is a very cool experiment class
    static class ExperimentClass {

        // Valid
        public void methodA(String s1, String s2) {};
        public void methodB(Integer s1, Integer s2) {};
        public void methodC(DSLContext DSLContext) {};
        public void methodF(String s1, DSLContext DSLContext) {};
        public void methodG(@Context("key1") String s1) {};
        public void methodH(String s1, @Context("key1") String s2) {};
        public void methodJ(Double s1, Integer s2) {};
        public void methodK(@DateArgs("dd/mm/YYYY") Date date) {};

        // Not Valid
        public void methodD(DSLContext DSLContext, String s1) {};
        public void methodE(DSLContext DSLContext, DSLContext DSLContext2) {};
        public void methodI( @Context("key1") String s1, String s2) {};
    }

}
