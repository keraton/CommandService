package me.bbr.client.config;

import com.github.keraton.easydsl.config.EasyDSLAppConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration()
@ComponentScan("me.bbr.client")
@Import(EasyDSLAppConfig.class)
public class AppConfig {
}
