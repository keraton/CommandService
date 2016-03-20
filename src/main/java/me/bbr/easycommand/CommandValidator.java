package me.bbr.easycommand;

import me.bbr.easycommand.annotation.Command;
import me.bbr.easycommand.annotation.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Component
public class CommandValidator {

    private Log LOG = LogFactory.getLog(CommandValidator.class);

    @Autowired
    private TypeExtractor typeExtractor;

    public final String REGEX = "^\\^(.*)\\$$";
    private Pattern pattern = Pattern.compile(REGEX);

    public boolean isValid(Method method, String pattern) {
        if (notAuthorizeArguments(method, pattern)) {
            LOG.warn("The argument and pattern doesn't match");
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

    private boolean contextIsNotLastPlace(Method method) {
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

    private boolean containsContextAnnotation(Annotation[] ann) {
        for (Annotation annotation: ann) {
            if (annotation.annotationType().equals(Context.class)) {
                return true;
            }
        }
        return false;
    }

    private int numberOfNonGroupParam(Method method) {
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

    private int addIfIsAContext(Annotation[] ann) {
        int counter = 0;
        for (Annotation annotation : ann) {
            if (annotation.annotationType().equals(Context.class)) {
                counter++;
            }
        }
        return counter;
    }

    private boolean notAuthorizeArguments(Method method, String pattern) {
        Class<?>[] types = method.getParameterTypes();
        List<Class> clazz = typeExtractor.extract(pattern);

        for (int i=0; i<types.length && i<clazz.size(); i++) {
            Class argument = types[i];
            Class expected = clazz.get(i);

            if (!argument.equals(expected)) {
                return true;
            }
        }
        return false;
    }

    public int countGroup(String patternToCount) {
        Pattern pattern = Pattern.compile(patternToCount);
        return pattern.matcher("").groupCount();
    }

    public boolean isValid(Method method) {
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

    public boolean isValid(Command annotation) {
        if (!isValid(annotation.value())) {
            LOG.warn("Pattern is invalid : correct pattern is " + REGEX);
            return false;
        }
        return true;
    }

    public boolean isValid(String patternToValidate) {
        if (patternSyntaxIsNotValid(patternToValidate)) {
            return false;
        }

        return pattern.matcher(patternToValidate).find();
    }

    private boolean patternSyntaxIsNotValid(String patternToValidate) {
        try {
            Pattern.compile(patternToValidate);
        }
        catch (PatternSyntaxException e) {
            LOG.warn("PatternSyntaxException : " + e.getMessage());
            return true;
        }
        return false;
    }
}
