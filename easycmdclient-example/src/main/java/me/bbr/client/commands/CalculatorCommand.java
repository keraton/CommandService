package me.bbr.client.commands;

import me.bbr.easycommand.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class CalculatorCommand {

    @Command("^add (INT) (INT)$")
    public String add(Integer a, Integer b) {
        return "" + (a + b);
    }

}
