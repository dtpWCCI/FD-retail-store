package com.fantasydrawer.ecommerce.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Configure backoff policy
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(1000L); // 1-second delay between retries
        retryTemplate.setBackOffPolicy(backOffPolicy);

        // Configure retry policy
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3); // Maximum 3 attempts
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}
