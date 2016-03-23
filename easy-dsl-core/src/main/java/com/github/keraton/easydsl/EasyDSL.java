package com.github.keraton.easydsl;

import com.github.keraton.easydsl.dto.DSLBeanMethod;
import com.github.keraton.easydsl.repository.DSLRepo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Service
public class EasyDSL {

    private static Log LOG = LogFactory.getLog(EasyDSL.class);

    @Autowired
    private ConfigurableListableBeanFactory configurable;

    @Autowired
    private DSLRepo DSLRepo;

    @Autowired
    private TypeExtractor typeExtractor;

    public String execute(String text) {
        return execute(text, null);
    }

    public String execute (String text, DSLContext DSLContext) {
        List<DSLBeanMethod> commands = DSLRepo.getAll();
        String result = null;

        for (DSLBeanMethod DSLBeanMethod : commands) {
            if (DSLBeanMethod.isMatch(text)) {

                List<Object> arguments = getArguments(text, DSLContext, DSLBeanMethod);
                Method method = DSLBeanMethod.getMethod();
                Object bean = getBean(DSLBeanMethod);
                try {
                    method.setAccessible(true);
                    result = (String) method.invoke(bean, arguments.toArray());
                    break;
                } catch (IllegalAccessException
                            | InvocationTargetException
                            | IllegalArgumentException e) {
                    LOG.warn("Method invoke failed : ", e);
                    result = "Usage : " + DSLBeanMethod.getDsl();
                }
            }
        }

        return result;
    }

    private List<Object> getArguments(String text, DSLContext DSLContext, DSLBeanMethod DSLBeanMethod) {
        List<Object> arguments = typeExtractor.extractArguments(DSLBeanMethod, text, DSLContext);

        if (DSLBeanMethod.isContainsCommandContext()) {
            arguments.add(DSLContext);
        }

        return arguments;
    }

    private Object getBean(DSLBeanMethod DSLBeanMethod) {
        String beanName = DSLBeanMethod.getBeanName();
        Class type = DSLBeanMethod.getType();

        return configurable.getBean(beanName, type);
    }
}
