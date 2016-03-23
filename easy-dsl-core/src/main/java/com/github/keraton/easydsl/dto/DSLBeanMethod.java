package com.github.keraton.easydsl.dto;

import com.github.keraton.easydsl.DSLContext;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class DSLBeanMethod {

    private final Method method;
    private final String dsl;
    private final String beanName;
    private final Class type;
    private final Pattern pattern;

    public DSLBeanMethod(String dsl, Method method, String beanName, Class type) {
        this.method = method;
        this.dsl = PatternTypeCollection.replaceWithMainPattern(dsl);
        this.beanName = beanName;
        this.type = type;
        this.pattern = compile(this.dsl);
    }

    public Method getMethod() {
        return method;
    }

    public String getDsl() {
        return dsl;
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
            if (clazz == DSLContext.class) {
                return true;
            }
        }
        return false;
    }

}
