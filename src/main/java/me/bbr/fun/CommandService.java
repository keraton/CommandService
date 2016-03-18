package me.bbr.fun;

import me.bbr.fun.repository.CommandRepo;
import me.bbr.fun.dto.CommandBeanMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommandService {

    private static Log LOG = LogFactory.getLog(CommandService.class);

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private CommandRepo commandRepo;

    public String execute(String text) {
        return execute(text, null);
    }

    public String execute (String text, CommandContext commandContext) {
        List<CommandBeanMethod> commands = commandRepo.getAll();
        String result = null;

        for (CommandBeanMethod commandBeanMethod: commands) {
            if (commandBeanMethod.isMatch(text)) {

                List<Object> arguments = getArguments(text, commandContext, commandBeanMethod);
                Method method = commandBeanMethod.getMethod();
                Object bean = getBean(commandBeanMethod);
                try {
                    method.setAccessible(true);
                    result = (String) method.invoke(bean, arguments.toArray());
                    break;
                } catch (IllegalAccessException
                            | InvocationTargetException
                            | IllegalArgumentException e) {
                    LOG.warn("Method invoke failed : ", e);
                    result += "Usage : " + commandBeanMethod.getCommand();
                }
            }
        }

        return result;
    }

    private List<Object> getArguments(String text, CommandContext commandContext, CommandBeanMethod commandBeanMethod) {
        List<String> arguments = commandBeanMethod.extractArguments(text, commandContext);

        List<Object> argumentsWithContext = new ArrayList<>();
        argumentsWithContext.addAll(arguments);

        if (commandBeanMethod.isContainsCommandContext()) {
            argumentsWithContext.add(commandContext);
        }

        return argumentsWithContext;
    }



    private Object getBean(CommandBeanMethod commandBeanMethod) {
        String beanName = commandBeanMethod.getBeanName();
        Class type = commandBeanMethod.getType();

        ConfigurableListableBeanFactory configurable = ((AbstractApplicationContext) ctx).getBeanFactory();
        return configurable.getBean(beanName, type);
    }
}
