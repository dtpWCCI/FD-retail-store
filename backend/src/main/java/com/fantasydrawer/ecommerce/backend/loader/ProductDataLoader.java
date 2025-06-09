package com.fantasydrawer.ecommerce.backend.loader;

import com.fantasydrawer.ecommerce.backend.service.ProductService;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductDataLoader implements CommandLineRunner {

    private final ProductService productService;
    private final Flyway flyway;

    public ProductDataLoader(ProductService productService, Flyway flyway) {
        this.productService = productService;
        this.flyway = flyway;
    }

    @Override
    public void run(String... args) throws Exception {
        // Run Flyway migration explicitly to ensure DB schema is initialized
        flyway.migrate();
        // Then load the products
        productService.fetchAndSaveProducts();
    }
}
