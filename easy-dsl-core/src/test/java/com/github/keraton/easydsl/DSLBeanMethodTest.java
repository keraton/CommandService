package com.github.keraton.easydsl;

import com.github.keraton.easydsl.dto.DSLBeanMethod;
import org.junit.Test;

import java.util.regex.PatternSyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class DSLBeanMethodTest {

    private DSLBeanMethod DSLBeanMethod = getCommandBeanMethod("^I love (.*) and (.*)$");

    private DSLBeanMethod getCommandBeanMethod(String command) {
        class Test {
            public String method(){
                return null;
            }
        }
        try {
            return new DSLBeanMethod(command, Test.class.getMethod("method"), null, null);
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
        assertThat(DSLBeanMethod
                        .isMatch("I love Paris and New York")).isEqualTo(true);
    }

    @Test
    public void should_not_match() {
        assertThat(DSLBeanMethod.isMatch("I love Paris")).isEqualTo(false);
    }

}