package me.bbr.client.config;

import me.bbr.easycommand.config.CommandAppConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration()
@ComponentScan("me.bbr.client")
@Import(CommandAppConfig.class)
public class AppConfig {
}
