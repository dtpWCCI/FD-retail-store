package com.fantasydrawer.ecommerce.backend.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fantasydrawer.ecommerce.backend.service.ProductService;

@Component
public class ProductDataLoader implements CommandLineRunner {

    @Autowired
    private ProductService productService;

    @Override
    public void run(String... args) throws Exception {
        productService.fetchAndSaveProducts();
    }
}