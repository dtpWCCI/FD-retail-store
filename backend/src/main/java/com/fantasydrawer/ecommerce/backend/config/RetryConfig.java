package com.fantasydrawer.ecommerce.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.retry.annotation.RetryConfiguration;
import java.util.Objects;

@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(1000L); // 1 second
        template.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3); // 3 attempts
        template.setRetryPolicy(retryPolicy);

        return template;
    }

    // Add the RetryInfrastructureConfig constructor here
    public RetryConfig(BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        
        scanner.findCandidateComponents("org.springframework.retry").stream()
            .filter(beanDefinition -> {
                String className = beanDefinition.getBeanClassName();
                return className != null && className.equals(RetryConfiguration.class.getName());
            })
            .forEach(beanDefinition -> {
                beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                registry.registerBeanDefinition(
                    Objects.requireNonNull(beanDefinition.getBeanClassName(), "beanClassName is null"),
                    beanDefinition);
            });
    }
}
