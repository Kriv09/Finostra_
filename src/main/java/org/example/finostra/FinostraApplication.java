package org.example.finostra;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinostraApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinostraApplication.class, args);
    }
}
