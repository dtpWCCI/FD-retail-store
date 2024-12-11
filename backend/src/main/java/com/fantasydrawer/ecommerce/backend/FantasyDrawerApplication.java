package com.fantasydrawer.ecommerce.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EntityScan(basePackages = {"com.fantasydrawer.ecommerce.backend.model"})
@EnableRetry
public class FantasyDrawerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FantasyDrawerApplication.class, args);
    }
}
