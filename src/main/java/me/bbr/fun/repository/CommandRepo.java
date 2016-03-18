package me.bbr.fun.repository;

import me.bbr.fun.dto.CommandBeanMethod;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CommandRepo {

    private List<CommandBeanMethod> commandBeanMethods = new ArrayList<>();

    public void save(List<CommandBeanMethod> commandBeanMethods) {
        this.commandBeanMethods.addAll(commandBeanMethods);
    }

    public List<CommandBeanMethod> getAll() {
        return this.commandBeanMethods;
    }
}
