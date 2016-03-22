package me.bbr.easycommand.dto;

import me.bbr.easycommand.CommandContext;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class CommandBeanMethod {

    private final Method method;
    private final String command;
    private final String beanName;
    private final Class type;
    private final Pattern pattern;

    public CommandBeanMethod(String command, Method method, String beanName, Class type) {
        this.method = method;
        this.command = PatternTypeCollection.replaceWithMainPattern(command);
        this.beanName = beanName;
        this.type = type;
        this.pattern = compile(this.command);
    }

    public Method getMethod() {
        return method;
    }

    public String getCommand() {
        return command;
    }

    public String getBeanName() {
        return beanName;
    }

    public Class getType() {
        return type;
    }

    public boolean isMatch(String text){
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public boolean isContainsCommandContext() {
        Class<?>[] types = method.getParameterTypes();
        for(Class clazz : types) {
            if (clazz == CommandContext.class) {
                return true;
            }
        }
        return false;
    }

}
