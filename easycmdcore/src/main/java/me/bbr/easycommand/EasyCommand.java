package me.bbr.easycommand;

import me.bbr.easycommand.dto.CommandBeanMethod;
import me.bbr.easycommand.repository.CommandRepo;
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
public class EasyCommand {

    private static Log LOG = LogFactory.getLog(EasyCommand.class);

    @Autowired
    private ConfigurableListableBeanFactory configurable;

    @Autowired
    private CommandRepo commandRepo;

    @Autowired
    private TypeExtractor typeExtractor;

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
                    result = "Usage : " + commandBeanMethod.getCommand();
                }
            }
        }

        return result;
    }

    private List<Object> getArguments(String text, CommandContext commandContext, CommandBeanMethod commandBeanMethod) {
        List<Object> arguments = typeExtractor.extractArguments(commandBeanMethod, text, commandContext);

        if (commandBeanMethod.isContainsCommandContext()) {
            arguments.add(commandContext);
        }

        return arguments;
    }

    private Object getBean(CommandBeanMethod commandBeanMethod) {
        String beanName = commandBeanMethod.getBeanName();
        Class type = commandBeanMethod.getType();

        return configurable.getBean(beanName, type);
    }
}
