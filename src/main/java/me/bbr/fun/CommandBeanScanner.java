package me.bbr.fun;

import me.bbr.fun.annotation.CommandSpec;
import me.bbr.fun.dto.CommandBeanMethod;
import me.bbr.fun.repository.CommandRepo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

@Component
public class CommandBeanScanner {

    private static Log LOG = LogFactory.getLog(CommandBeanScanner.class);

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private CommandRepo commandRepo;

    List<CommandBeanMethod> commandBeanMethods = new ArrayList<>();

    @PostConstruct
    public void scan() {
        String[] all = ctx.getBeanDefinitionNames();

        ConfigurableListableBeanFactory configurable = ((AbstractApplicationContext) ctx).getBeanFactory();
        for (String name : all) {
            Object bean = configurable.getBean(name);
            if (bean != null) {
                scanBean(bean, name);
            }
        }
        commandRepo.save(commandBeanMethods);
    }

    private void scanBean(Object bean, String beanName) {
        Method[] methods = bean.getClass().getMethods();
        for (Method method: methods) {
            scan(method, beanName, bean.getClass());
        }
    }

    private void scan(Method method, String beanName, Class type) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation: annotations) {
            LOG.info("Check validity on : " +  beanName + "." + method.getName());
            if (annotation instanceof CommandSpec
                    && CommandValidator.isValid(method)
                    && CommandValidator.isValid((CommandSpec) annotation)
                    && CommandValidator.isValid(method, ((CommandSpec) annotation).value())
                    ) {
                addSpec((CommandSpec) annotation, beanName, type, method);

                LOG.info("Bean : " +  beanName + "." + method.getName() + "is valid");
            }
            else {
                LOG.warn("Bean : " + beanName + "." + method.getName()
                        + " is not valid, please check on the warning");
            }
        }
    }

    private void addSpec(CommandSpec commandSpec, String beanName, Class type, Method method) {
        try {
            commandBeanMethods.add(new CommandBeanMethod(commandSpec.value(), method, beanName, type));
        }
        catch (PatternSyntaxException e) {
            LOG.warn("PatternSyntaxException for " + beanName + " (" + type + "), command " +  commandSpec.value(), e);
        }
    }
}
