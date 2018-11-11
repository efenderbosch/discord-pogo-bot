package net.fender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication(scanBasePackages = "net.fender")
public class Giovanni {

    private static final Logger LOG = LoggerFactory.getLogger(Giovanni.class);

    public static void main(String[] args) {
        SpringApplication.run(Giovanni.class, args);
    }
}
