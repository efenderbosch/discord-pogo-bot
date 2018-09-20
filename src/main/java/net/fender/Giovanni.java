package net.fender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication(scanBasePackages = "net.fender")
public class Giovanni {
    public static void main(String[] args) {
        SpringApplication.run(Giovanni.class, args);
    }
}
