package me.bbr.easycommand;

import me.bbr.easycommand.annotation.Context;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandValidatorTest {

    @Test
    public void should_count_only_one_group () {
        // Then
        assertThat(CommandValidator.countGroup("Group one (\\w+)")).isEqualTo(1);
        assertThat(CommandValidator.countGroup("Group two (\\w+) (\\w+)")).isEqualTo(2);
    }

    @Test
    public void should_match_only_one_line_pattern () {
        // Should start with ^ and end with $
        assertThat(CommandValidator.isValid("^This should work$")).isTrue();
        // Should end with $
        assertThat(CommandValidator.isValid("^This should not work")).isFalse();
        // Should start with ^
        assertThat(CommandValidator.isValid("This should not work$")).isFalse();
        // Should stay one line
        assertThat(CommandValidator.isValid("^This should not \n work$")).isFalse();
    }

    @Test
    public void should_valid_with_strings () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodA", String.class, String.class);

        // When
        boolean valid = CommandValidator.isValid(methodA, "^ With two (\\w+) (\\w+) $");

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void should_not_valid_with_bad_compilation () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodA", String.class, String.class);

        // When
        boolean valid = CommandValidator.isValid("^ With two (*.) $");

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    public void should_not_valid_with_different_size_of_argument () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodA", String.class, String.class);

        // When
        boolean valid = CommandValidator.isValid(methodA, "^ With one (\\w+) $");

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    public void should_not_valid_with_int () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodB", int.class, int.class);

        // When
        boolean valid = CommandValidator.isValid(methodA, "^ With two (\\w+) (\\w+) $");

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    public void should_valid_with_one_CommandContext () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodC", CommandContext.class);

        // When
        boolean valid = CommandValidator.isValid(methodA, "^This should be enough$");

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void should_not_valid_with_CommandContext_not_in_the_last () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodD", CommandContext.class, String.class);

        // When
        boolean valid = CommandValidator.isValid(methodA, "^(\\w+)$");

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    public void should_valid_with_CommandContext_in_the_last () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodF", String.class, CommandContext.class);

        // When
        boolean valid = CommandValidator.isValid(methodA, "^(\\w+)$");

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void should_not_valid_with_moreThanOne_CommandContext () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodE", CommandContext.class, CommandContext.class);

        // When
        boolean valid = CommandValidator.isValid(methodA, "^ $");

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    public void should_valid_with_string_annotate () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodG", String.class);

        // When
        boolean valid = CommandValidator.isValid(methodA, "^ $");

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void should_valid_with_string_annotate_in_the_last_place () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodH", String.class, String.class);

        // When
        boolean valid = CommandValidator.isValid(methodA, "^ (\\w+) $");

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void should_not_valid_with_string_annotate_notIn_the_last_place () throws NoSuchMethodException {
        // Given
        Method methodA = ExperimentClass.class.getMethod("methodI", String.class, String.class);

        // When
        boolean valid = CommandValidator.isValid(methodA, "^ (\\w+) $");

        // Then
        assertThat(valid).isFalse();
    }

    // This is a very cool experiment class
    static class ExperimentClass {

        // Valid
        public void methodA(String s1, String s2) {};
        public void methodC(CommandContext commandContext) {};
        public void methodF(String s1, CommandContext commandContext) {};
        public void methodG(@Context("key1") String s1) {};
        public void methodH(String s1, @Context("key1") String s2) {};

        // Not Valid
        public void methodB(int s1, int s2) {};
        public void methodD(CommandContext commandContext, String s1) {};
        public void methodE(CommandContext commandContext, CommandContext commandContext2) {};
        public void methodI( @Context("key1") String s1, String s2) {};
    }

}
