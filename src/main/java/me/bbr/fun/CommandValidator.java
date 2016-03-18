package me.bbr.fun;

import me.bbr.fun.annotation.CommandSpec;
import me.bbr.fun.annotation.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class CommandValidator {

    private static Log LOG = LogFactory.getLog(CommandValidator.class);

    public static final String REGEX = "^\\^(.*)\\$$";
    private static Pattern pattern = Pattern.compile(REGEX);

    public static boolean isValid(String patternToValidate) {
        return pattern.matcher(patternToValidate).find();
    }

    public static int countGroup(String patternToCount) {
        Pattern pattern = Pattern.compile(patternToCount);
        return pattern.matcher("").groupCount();
    }

    public static boolean isValid(Method method, String pattern) {
        if (notAuthorizeArguments(method)) {
            LOG.warn("Only String arguments is accepted");
            return false;
        }

        if (contextIsNotLastPlace(method)) {
            LOG.warn("Context should be in the last place");
            return false;
        }

        int parameterCount = method.getParameterCount() - numberOfNonGroupParam(method);
        if (parameterCount != countGroup(pattern)) {
            LOG.warn("Number argument is different with pattern");
            return false;
        }

        return true;
    }

    private static boolean contextIsNotLastPlace(Method method) {
        for (int index=0; index < method.getParameterTypes().length; index++) {
            Class c = method.getParameterTypes()[index];
            Annotation[] ann = method.getParameterAnnotations()[index];
            if ((CommandContext.class.equals(c) || containsContextAnnotation(ann))
                    && index < method.getParameterTypes().length - 1) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsContextAnnotation(Annotation[] ann) {
        for (Annotation annotation: ann) {
            if (annotation.annotationType().equals(Context.class)) {
                return true;
            }
        }
        return false;
    }

    private static int numberOfNonGroupParam(Method method) {
        int counter = 0;
        for (int i=0; i<method.getParameterTypes().length; i++) {
            Class c = method.getParameterTypes()[i];
            Annotation[] ann = method.getParameterAnnotations()[i];
            if (CommandContext.class.equals(c)) {
                counter+=1;
            }
            else {
                counter += addIfIsAContext(ann);
            }
        }
        return counter;
    }

    private static int addIfIsAContext(Annotation[] ann) {
        int counter = 0;
        for (Annotation annotation : ann) {
            if (annotation.annotationType().equals(Context.class)) {
                counter++;
            }
        }
        return counter;
    }

    private static boolean notAuthorizeArguments(Method method) {
        Class<?>[] types = method.getParameterTypes();
        for (Class c: types) {
            if (!c.equals(String.class) && !CommandContext.class.equals(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValid(Method method) {
        if (method.getReturnType() == null) {
            LOG.warn("Method invalid, cannot be void");
            return false;
        }
        if (!method.getReturnType().equals(String.class)) {
            LOG.warn("Method invalid, return type is not String");
            return false;
        }

        return true;
    }

    public static boolean isValid(CommandSpec annotation) {
        if (!isValid(annotation.value())) {
            LOG.warn("Pattern is invalid : correct pattern is " + REGEX);
            return false;
        }
        return true;
    }
}
