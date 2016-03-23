package com.github.keraton.easydsl;

import com.github.keraton.easydsl.annotation.DSL;
import com.github.keraton.easydsl.dto.DSLBeanMethod;
import com.github.keraton.easydsl.repository.DSLRepo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DSLBeanScanner implements BeanPostProcessor {

    private static Log LOG = LogFactory.getLog(DSLBeanScanner.class);

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private DSLRepo DSLRepo;

    @Autowired
    private DSLValidator dslValidator;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        List<DSLBeanMethod> DSLBeanMethods = scanBean(bean, beanName);
        DSLRepo.addAll(DSLBeanMethods);
        return bean;
    }

    private List<DSLBeanMethod> scanBean(Object bean, String beanName) {
        List<DSLBeanMethod> DSLBeanMethods = new ArrayList<>();
        Method[] methods = bean.getClass().getMethods();
        for (Method method: methods) {
            Optional<DSLBeanMethod> commandBeanMethodOptional = scan(method, beanName, bean.getClass());
            if (commandBeanMethodOptional.isPresent()) {
                DSLBeanMethods.add(commandBeanMethodOptional.get());
            }
        }
        return DSLBeanMethods;
    }

    private Optional<DSLBeanMethod> scan(Method method, String beanName, Class type) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation: annotations) {
            if (annotation instanceof DSL) {
                LOG.info("Check validity on : " + beanName + "." + method.getName());

                if (dslValidator.isValid(method)
                        && dslValidator.isValid((DSL) annotation)
                        && dslValidator.isValid(method, ((DSL) annotation).value())
                        ) {
                    LOG.info("Bean : " + beanName + "." + method.getName() + " is added");
                    return addSpec((DSL) annotation, method, beanName, type);

                } else {
                    LOG.warn("Bean : " + beanName + "." + method.getName()
                            + " is not valid, please check on the warning");
                }
            }
        }
        return Optional.empty();
    }

    private Optional<DSLBeanMethod> addSpec(DSL DSL, Method method, String beanName, Class type) {
        return Optional.of(new DSLBeanMethod(DSL.value(), method, beanName, type));
    }
}
