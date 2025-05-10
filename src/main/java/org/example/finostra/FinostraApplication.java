package org.example.finostra;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.example.finostra.Repositories")
public class FinostraApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinostraApplication.class, args);
    }
}
