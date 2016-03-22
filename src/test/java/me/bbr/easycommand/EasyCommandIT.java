package me.bbr.easycommand;


import me.bbr.easycommand.annotation.Command;
import me.bbr.easycommand.annotation.Context;
import me.bbr.easycommand.annotation.DateArgs;
import me.bbr.easycommand.config.CommandAppConfig;
import me.bbr.easycommand.dto.CommandBeanMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
public class EasyCommandIT {

    @Autowired
    private CommandBeanScanner scanner;

    @Autowired
    private EasyCommand easyCommand;

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Test
    public void should_scan () {
        // Given
        List<CommandBeanMethod> commandBeanMethods = scanner.commandBeanMethods;

        // Then
        assertThat(commandBeanMethods).hasSize(3);
    }

    @Test
    public void should_service () {
        String result = easyCommand.execute("I love Paris and London and number 3 1.0 and date 06/03/2016");

        assertThat(result).isEqualTo("Paris" + "London" + 3 + ""+ 1.0+ "06/03/2016");
    }

    @Test
    public void should_service_with_context () {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");

        String result = easyCommand.execute("I love Paris too", new CommandContext(map));

        assertThat(result).isEqualTo("Paris" + "value1");
    }

    @Test
    public void should_service_with_context_annotation () {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");

        String result = easyCommand.execute("I love Paris again", new CommandContext(map));

        assertThat(result).isEqualTo("Paris" + "value1");
    }

    static class ClassWithCommand {

        @Command("^I love (\\w+) and (\\w+) and number (\\d+) (DOUBLE) and date (.+)$")
        public String command(String text,
                              String text2,
                              Integer integer,
                              Double doubles,
                              @DateArgs("dd/MM/yyyy") Date date) {
            return text + text2 +integer + doubles + sdf.format(date);
        }

        @Command("^I love (\\w*) too$")
        public String commandWithContext(String text, CommandContext commandContext) {
            return text + commandContext.getHeader().get("key1");
        }

        @Command("^I love (\\w*) again$")
        public String commandWithContextAnnotation(String text, @Context("key1") String value) {
            return text + value;
        }

        @Command("^I love (.*)$")
        public Object command_invalid_1 (String text) {
            return text;
        }

        @Command("^I love (.*)$")
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