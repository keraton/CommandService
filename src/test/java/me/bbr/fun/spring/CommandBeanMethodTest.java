package me.bbr.fun.spring;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.regex.PatternSyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandBeanMethodTest {

    private CommandBeanMethod commandBeanMethod = getCommandBeanMethod("^I love (.*) and (.*)$");

    private CommandBeanMethod getCommandBeanMethod(String command) {
        return new CommandBeanMethod(command, null, null, null);
    }

    @Test(expected = PatternSyntaxException.class)
    public void failed_pattern_command_should_return_false () {
        getCommandBeanMethod("^I love (*.) and (*.)$");
    }

    @Test
    public void should_match() {
        assertThat(commandBeanMethod
                        .isMatch("I love Paris and New York")).isEqualTo(true);
    }

    @Test
    public void should_not_match() {
        assertThat(commandBeanMethod.isMatch("I love Paris")).isEqualTo(false);
    }

    @Test
    public void should_match_group() {
        assertThat(commandBeanMethod
                        .extractArguments("I love Paris and New York", null)).containsExactly("Paris", "New York");
    }

}