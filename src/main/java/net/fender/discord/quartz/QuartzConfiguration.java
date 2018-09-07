package net.fender.discord.quartz;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QuartzProperties.class)
public class QuartzConfiguration {

}
