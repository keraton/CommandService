package com.github.keraton.easydsl;


import com.github.keraton.easydsl.annotation.DSL;
import com.github.keraton.easydsl.annotation.Context;
import com.github.keraton.easydsl.annotation.DateArgs;
import com.github.keraton.easydsl.config.EasyDSLAppConfig;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
public class EasyDSLIT {

    @Autowired
    private DSLBeanScanner scanner;

    @Autowired
    private EasyDSL easyDSL;

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


    @Test
    public void should_service () {
        String result = easyDSL.execute("I love Paris and London and number 3 1.0 and date 06/03/2016 3 true");

        assertThat(result).isEqualTo("Paris" + "London" + 3 + ""+ 1.0+ "06/03/2016" + 3 + true);
    }

    @Test
    public void should_service_with_context () {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");

        String result = easyDSL.execute("I love Paris too", new DSLContext(map));

        assertThat(result).isEqualTo("Paris" + "value1");
    }

    @Test
    public void should_service_with_context_annotation () {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");

        String result = easyDSL.execute("I love Paris again", new DSLContext(map));

        assertThat(result).isEqualTo("Paris" + "value1");
    }

    static class ClassWithCommand {

        @DSL("^I love (\\w+) and (\\w+) and number (\\d+) (DOUBLE) and date (.+) (INT) (BOOLEAN)$")
        public String command(String text,
                              String text2,
                              Integer integer,
                              Double doubles,
                              @DateArgs("dd/MM/yyyy") Date date,
                              Integer integer2,
                              Boolean bool
                            ) {
            return text + text2 +integer + doubles + sdf.format(date) +integer2 + bool;
        }

        @DSL("^I love (\\w*) too$")
        public String commandWithContext(String text, DSLContext DSLContext) {
            return text + DSLContext.getHeader().get("key1");
        }

        @DSL("^I love (\\w*) again$")
        public String commandWithContextAnnotation(String text, @Context("key1") String value) {
            return text + value;
        }

        @DSL("^I love (.*)$")
        public Object command_invalid_1 (String text) {
            return text;
        }

        @DSL("^I love (.*)$")
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
    @Import(EasyDSLAppConfig.class)
    static class ContextConfiguration {

        @Bean ClassWithCommand classWithCommand() {
            return new ClassWithCommand();
        }

        @Bean ClassWithoutCommand classWithoutCommand() {
            return new ClassWithoutCommand();
        }

    }

}