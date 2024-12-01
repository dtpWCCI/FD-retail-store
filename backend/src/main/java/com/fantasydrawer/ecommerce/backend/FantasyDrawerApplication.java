package com.fantasydrawer.ecommerce.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;


@SpringBootApplication
@EnableRetry
public class FantasyDrawerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FantasyDrawerApplication.class, args);
    }
}
