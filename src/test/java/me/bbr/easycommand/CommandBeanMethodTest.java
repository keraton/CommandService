package me.bbr.easycommand;

import me.bbr.easycommand.dto.CommandBeanMethod;
import org.junit.Test;

import java.util.regex.PatternSyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandBeanMethodTest {

    private CommandBeanMethod commandBeanMethod = getCommandBeanMethod("^I love (.*) and (.*)$");

    private CommandBeanMethod getCommandBeanMethod(String command) {
        class Test {
            public String method(){
                return null;
            }
        }
        try {
            return new CommandBeanMethod(command, Test.class.getMethod("method"), null, null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
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

}