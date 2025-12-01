package com.eaglebank.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.eaglebank")
public class EaglebankApplication {

    public static void main(String[] args) {
        SpringApplication.run(EaglebankApplication.class, args);
    }
}
