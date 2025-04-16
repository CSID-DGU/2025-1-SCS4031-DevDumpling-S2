package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner checkBeans(ApplicationContext ctx) {
        return args -> {
            System.out.println("🔍 Beans loaded:");
            for (String name : ctx.getBeanDefinitionNames()) {
                if (name.toLowerCase().contains("controller")) {
                    System.out.println("👉 Found controller bean: " + name);
                }
            }
        };
    }
}
