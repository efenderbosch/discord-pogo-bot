package net.fender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "net.fender")
public class DiscordPogoOcrBot {

    public static void main(String[] args) {
        SpringApplication.run(DiscordPogoOcrBot.class, args);
    }
}
