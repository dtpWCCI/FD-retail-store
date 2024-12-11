package com.fantasydrawer.ecommerce.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fantasydrawer.ecommerce.backend.repository.ProductRepository;
import com.fantasydrawer.ecommerce.backend.service.ProductService;
import com.fantasydrawer.ecommerce.backend.service.impl.ProductServiceImpl;

@Configuration
public class ProductConfiguration {

    @Bean
    public ProductService productService(ProductRepository productRepository) {
        return new ProductServiceImpl();
    }
}
