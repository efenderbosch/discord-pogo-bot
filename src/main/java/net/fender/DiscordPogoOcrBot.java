package net.fender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "net.fender")
public class DiscordPogoOcrBot {

    private static final Logger LOG = LoggerFactory.getLogger(DiscordPogoOcrBot.class);

    public static void main(String[] args) {
        SpringApplication.run(DiscordPogoOcrBot.class, args);
    }
}
