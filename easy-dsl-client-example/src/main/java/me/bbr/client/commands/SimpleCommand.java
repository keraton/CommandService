package me.bbr.client.commands;

import com.github.keraton.easydsl.annotation.DSL;
import org.springframework.stereotype.Component;

@Component
public class SimpleCommand {

    @DSL("^hello$")
    public String helloWorld(){
        return "hello world";
    }

    @DSL("^how are you$")
    public String howAreYou(){
        return "I'm fine thanks you";
    }

    @DSL("^I love (\\w+)$")
    public String iLove(String text){
        return "I love " + text + " too";
    }

    @DSL("^help me$")
    public String help(){
        return "What can I do for you?";
    }
}
