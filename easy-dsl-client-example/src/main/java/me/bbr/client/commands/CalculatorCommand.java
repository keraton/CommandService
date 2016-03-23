package me.bbr.client.commands;

import com.github.keraton.easydsl.annotation.DSL;
import org.springframework.stereotype.Component;

@Component
public class CalculatorCommand {

    @DSL("^add (INT) (INT)$")
    public String add(Integer a, Integer b) {
        return "" + (a + b);
    }

}
