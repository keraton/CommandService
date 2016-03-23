package me.bbr.client.commands;

import me.bbr.easycommand.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class SimpleCommand {

    @Command("^hello$")
    public String helloWorld(){
        return "hello world";
    }

    @Command("^how are you$")
    public String howAreYou(){
        return "I'm fine thanks you";
    }

    @Command("^I love (\\w+)$")
    public String iLove(String text){
        return "I love " + text + " too";
    }

    @Command("^help me$")
    public String help(){
        return "What can I do for you?";
    }
}
