package me.bbr.easycommand.repository;

import me.bbr.easycommand.dto.CommandBeanMethod;
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
