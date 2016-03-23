package me.bbr.easycommand;

import me.bbr.easycommand.annotation.Command;
import me.bbr.easycommand.dto.CommandBeanMethod;
import me.bbr.easycommand.repository.CommandRepo;
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
public class CommandBeanScanner implements BeanPostProcessor {

    private static Log LOG = LogFactory.getLog(CommandBeanScanner.class);

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private CommandRepo commandRepo;

    @Autowired
    private CommandValidator commandValidator;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        List<CommandBeanMethod> commandBeanMethods = scanBean(bean, beanName);
        commandRepo.addAll(commandBeanMethods);
        return bean;
    }

    private List<CommandBeanMethod> scanBean(Object bean, String beanName) {
        List<CommandBeanMethod> commandBeanMethods = new ArrayList<>();
        Method[] methods = bean.getClass().getMethods();
        for (Method method: methods) {
            Optional<CommandBeanMethod> commandBeanMethodOptional = scan(method, beanName, bean.getClass());
            if (commandBeanMethodOptional.isPresent()) {
                commandBeanMethods.add(commandBeanMethodOptional.get());
            }
        }
        return commandBeanMethods;
    }

    private Optional<CommandBeanMethod> scan(Method method, String beanName, Class type) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation: annotations) {
            if (annotation instanceof Command) {
                LOG.info("Check validity on : " + beanName + "." + method.getName());

                if (commandValidator.isValid(method)
                        && commandValidator.isValid((Command) annotation)
                        && commandValidator.isValid(method, ((Command) annotation).value())
                        ) {
                    LOG.info("Bean : " + beanName + "." + method.getName() + " is added");
                    return addSpec((Command) annotation, method, beanName, type);

                } else {
                    LOG.warn("Bean : " + beanName + "." + method.getName()
                            + " is not valid, please check on the warning");
                }
            }
        }
        return Optional.empty();
    }

    private Optional<CommandBeanMethod> addSpec(Command command, Method method, String beanName, Class type) {
        return Optional.of(new CommandBeanMethod(command.value(), method, beanName, type));
    }
}
