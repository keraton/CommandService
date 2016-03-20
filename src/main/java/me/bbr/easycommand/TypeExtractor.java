package me.bbr.easycommand;

import me.bbr.easycommand.annotation.Context;
import me.bbr.easycommand.annotation.DateArgs;
import me.bbr.easycommand.dto.CommandBeanMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TypeExtractor {

    private static Log LOG = LogFactory.getLog(TypeExtractor.class);

    private Pattern patternOfPattern = Pattern.compile(
                                                "(?<word>\\(\\\\w\\+\\))|"                      +
                                                "(?<int>\\(\\\\d\\+\\))|"                       +
                                                "(?<anything>\\(\\.\\*\\))|"                    +
                                                "(?<anything2>\\(\\.\\+\\))|"                   +
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

        groups.addAll(extractFromPattern(commandBeanMethod, text, pattern));
        groups.addAll(extractFromContext(commandBeanMethod, commandContext));

        return groups;
    }

    private List<Object> extractFromContext(CommandBeanMethod commandBeanMethod, CommandContext commandContext) {
        List<Object> groups = new ArrayList<>();
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

    private List<Object> extractFromPattern(CommandBeanMethod commandBeanMethod, String text, Pattern pattern) {
        List<Class> types = this.extract(commandBeanMethod.getCommand());
        List<Object> groups = new ArrayList<>();
        Annotation[][] annotations = commandBeanMethod.getMethod().getParameterAnnotations();

        Matcher matcher = pattern.matcher(text);

        if (matcher.matches()) {
            // No need to take the 1st match
            for(int i=1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                Class type = getAClass(types, i);
                Annotation[] anns = annotations[i-1];

                if (Integer.class.equals(type)) {
                    groups.add(Integer.valueOf(group));
                }
                else if (Double.class.equals(type)) {
                    groups.add(Double.valueOf(group));
                }
                else if (getDateArgsAnnotation(anns) != null && String.class.equals(type)) {
                    groupAddDate(groups, group, anns);
                }
                else {
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    private void groupAddDate(List<Object> groups, String group, Annotation[] anns) {
        String format = getDateArgsAnnotation(anns).value();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(group);
            groups.add(date);
        } catch (ParseException e) {
            LOG.warn("Error parse exception", e);
            groups.add(null);
        }
    }

    private DateArgs getDateArgsAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof DateArgs) {
                return (DateArgs) annotation;
            }
        }
        return null;
    }

    private Class getAClass(List<Class> types, int i) {
        int indexForClass = i - 1;
        return indexForClass < types.size() ? types.get(indexForClass) : null;
    }


}
