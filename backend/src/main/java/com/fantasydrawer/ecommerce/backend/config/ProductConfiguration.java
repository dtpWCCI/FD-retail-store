package com.fantasydrawer.ecommerce.backend.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fantasydrawer.ecommerce.backend.service.ProductService;
import com.fantasydrawer.ecommerce.backend.service.impl.ProductServiceImpl;

@Configuration
public class ProductConfiguration {


	@Bean
    @Primary
	public ProductService productService(ProductServiceImpl productServiceImpl) {
	    return productServiceImpl;
	}
}
