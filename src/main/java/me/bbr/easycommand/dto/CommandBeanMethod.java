package me.bbr.easycommand.dto;

import me.bbr.easycommand.CommandContext;
import me.bbr.easycommand.annotation.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
        this.command = command;
        this.beanName = beanName;
        this.type = type;
        this.pattern = compile(command);
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

    public List<String> extractArguments(String text, CommandContext commandContext) {
        List<String> groups = new ArrayList<>();

        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            // No need to take the 1st match
            for(int i=1; i <= matcher.groupCount(); i++) {
                groups.add(matcher.group(i));
            }
        }

        // Need to add annotation
        if (method != null) {
            Annotation[][] annotationParam = this.method.getParameterAnnotations();
            for (Annotation[] ann : annotationParam) {
                for (Annotation an : ann) {
                    if (an instanceof Context) {
                        Context context = (Context) an;
                        groups.add(commandContext.getHeader().get(context.value()));
                    }
                }
            }
        }

        return groups;
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
