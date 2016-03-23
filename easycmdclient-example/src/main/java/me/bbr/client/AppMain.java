package me.bbr.client;

import me.bbr.client.config.AppConfig;
import me.bbr.easycommand.EasyCommand;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Scanner;

import static java.lang.System.in;
import static java.lang.System.out;

public class AppMain {

    public static void main(String... args) throws InterruptedException {
        // Start spring
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        // Start easy command
        EasyCommand easyCommand = applicationContext.getBean(EasyCommand.class);

        // Start scanner
        Scanner sc = new Scanner(in);

        // Start console
        out.println("--> Write your command here");

        // Wait for command
        while(sc.hasNextLine()) {
            String nextLine = sc.nextLine();

            String execute = easyCommand.execute(nextLine);
            if (execute == null) {
                out.println(String.format("%s command is unknown", nextLine));
            }
            else {
                out.println(execute);
            }
        }
    }
}
