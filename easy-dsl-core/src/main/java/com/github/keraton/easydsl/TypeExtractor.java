package com.github.keraton.easydsl;

import com.github.keraton.easydsl.annotation.Context;
import com.github.keraton.easydsl.annotation.DateArgs;
import com.github.keraton.easydsl.dto.DSLBeanMethod;
import com.github.keraton.easydsl.dto.PatternType;
import com.github.keraton.easydsl.dto.PatternTypeCollection;
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
import java.util.stream.Collectors;

@Component
public class TypeExtractor {

    private static Log LOG = LogFactory.getLog(TypeExtractor.class);


    private static Pattern patternForGroup = Pattern.compile("(\\([^\\)]*)");


    public List<Class> extract(String pattern) {
        Matcher matcher = patternForGroup.matcher(pattern);
        List<Class> classes = new ArrayList<>();
        while(matcher.find()) {
            String group = matcher.group() + ")";
            List<Class> collect = PatternTypeCollection.getPatternList().stream()
                    .map(patternType -> getClassFromPattern(group, patternType))
                    .filter(c -> c != null)
                    .collect(Collectors.toList());

            if (collect.isEmpty()) {
                classes.add(String.class);
            }
            else {
                classes.addAll(collect);
            }
        }
        return classes;
    }

    private Class getClassFromPattern(String group, PatternType patternType) {
        for (String stringPattern : patternType.getAllPattern()){
            if (stringPattern.equals(group)) {
                return patternType.getClazz();
            }
        }
        return null;
    }

    public List<Object> extractArguments(DSLBeanMethod DSLBeanMethod, String text, DSLContext DSLContext) {
        Pattern pattern = Pattern.compile(DSLBeanMethod.getDsl());

        List<Object> groups = new ArrayList<>();

        groups.addAll(extractFromPattern(DSLBeanMethod, text, pattern));
        groups.addAll(extractFromContext(DSLBeanMethod, DSLContext));

        return groups;
    }

    private List<Object> extractFromContext(DSLBeanMethod DSLBeanMethod, DSLContext DSLContext) {
        List<Object> groups = new ArrayList<>();
        Annotation[][] annotationParam = DSLBeanMethod.getMethod().getParameterAnnotations();
        for (Annotation[] ann : annotationParam) {
            for (Annotation an : ann) {
                if (an instanceof Context) {
                    Context context = (Context) an;
                    groups.add(DSLContext.getHeader().get(context.value()));
                }
            }
        }
        return groups;
    }

    private List<Object> extractFromPattern(DSLBeanMethod DSLBeanMethod, String text, Pattern pattern) {
        List<Class> types = this.extract(DSLBeanMethod.getDsl());
        List<Object> groups = new ArrayList<>();
        Annotation[][] annotations = DSLBeanMethod.getMethod().getParameterAnnotations();

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
                else if (Boolean.class.equals(type)) {
                    groups.add(Boolean.valueOf(group));
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
