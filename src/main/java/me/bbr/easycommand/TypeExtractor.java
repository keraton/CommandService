package me.bbr.easycommand;

import me.bbr.easycommand.annotation.Context;
import me.bbr.easycommand.dto.CommandBeanMethod;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TypeExtractor {

    private Pattern patternOfPattern = Pattern.compile(
                                                "(?<word>\\(\\\\w\\+\\))|"                      +
                                                "(?<int>\\(\\\\d\\+\\))|"                       +
                                                "(?<anything>\\(\\.\\*\\))|"                    +
                                                "(?<decimal>\\(\\\\d\\+\\\\.\\\\d\\+\\))"
                                            );

    public List<Class> extract(String pattern) {
        Matcher matcher = patternOfPattern.matcher(pattern);
        List<Class> classes = new ArrayList<>();
        while(matcher.find()) {
            String group = matcher.group();
            if ("(\\d+)".equals(group)) {
                classes.add(Integer.class);
            }
            else if ("(\\d+\\.\\d+)".equals(group)) {
                classes.add(Double.class);
            }
            else {
                classes.add(String.class);
            }
        }
        return classes;
    }

    public List<Object> extractArguments(CommandBeanMethod commandBeanMethod, String text, CommandContext commandContext) {
        Pattern pattern = Pattern.compile(commandBeanMethod.getCommand());

        List<Object> groups = new ArrayList<>();
        List<Class> types = this.extract(commandBeanMethod.getCommand());

        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            // No need to take the 1st match
            for(int i=1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                Class type = getAClass(types, i);

                if (Integer.class.equals(type)) {
                    groups.add(Integer.valueOf(group));
                }
                else if (Double.class.equals(type)) {
                    groups.add(Double.valueOf(group));
                }
                else {
                    groups.add(group);
                }
            }
        }

        // Need to add annotation
        Annotation[][] annotationParam = commandBeanMethod.getMethod().getParameterAnnotations();
        for (Annotation[] ann : annotationParam) {
            for (Annotation an : ann) {
                if (an instanceof Context) {
                    Context context = (Context) an;
                    groups.add(commandContext.getHeader().get(context.value()));
                }
            }
        }

        return groups;
    }

    private Class getAClass(List<Class> types, int i) {
        int indexForClass = i - 1;
        return indexForClass < types.size() ? types.get(indexForClass) : null;
    }


}
