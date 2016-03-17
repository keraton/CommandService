package me.bbr.fun.spring;


import me.bbr.fun.CommandContext;
import me.bbr.fun.CommandService;
import me.bbr.fun.annotation.CommandSpec;
import me.bbr.fun.annotation.Context;
import me.bbr.fun.config.CommandAppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
public class CommandServiceIT {

    @Autowired
    private CommandBeanScanner scanner;

    @Autowired
    private CommandService commandService;

    @Test
    public void should_scan () {
        // Given
        List<CommandBeanMethod> commandBeanMethods = scanner.getCommandBeanMethods();

        // Then
        assertThat(commandBeanMethods).hasSize(3);
    }

    @Test
    public void should_service () {
        String result = commandService.execute("I love Paris and London");

        assertThat(result).isEqualTo("Paris" + "London");
    }

    @Test
    public void should_service_with_context () {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");

        String result = commandService.execute("I love Paris too", new CommandContext(map));

        assertThat(result).isEqualTo("Paris" + "value1");
    }

    @Test
    public void should_service_with_context_annotation () {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");

        String result = commandService.execute("I love Paris again", new CommandContext(map));

        assertThat(result).isEqualTo("Paris" + "value1");
    }

    static class ClassWithCommand {

        /**
         * WARNING : there is no possibility to check the size of the arguments
         */
        @CommandSpec("^I love (\\w*) and (\\w*)$")
        public String command(String text, String text2) {
            return text + text2;
        }

        @CommandSpec("^I love (\\w*) too$")
        public String commandWithContext(String text, CommandContext commandContext) {
            return text + commandContext.getHeader().get("key1");
        }

        @CommandSpec("^I love (\\w*) again$")
        public String commandWithContextAnnotation(String text, @Context("key1") String value) {
            return text + value;
        }

        @CommandSpec("^I love (.*)$")
        public Object command_invalid_1 (String text) {
            return text;
        }

        @CommandSpec("^I love (.*)$")
        public void command_invalid_2 (String text) {
        }

        public String notACommand() {
            return null;
        }

    }

    static class ClassWithoutCommand {

        public String notACommand() {
            return null;
        }

    }

    @Configuration
    @Import(CommandAppConfig.class)
    static class ContextConfiguration {

        @Bean ClassWithCommand classWithCommand() {
            return new ClassWithCommand();
        }

        @Bean ClassWithoutCommand classWithoutCommand() {
            return new ClassWithoutCommand();
        }

    }

}