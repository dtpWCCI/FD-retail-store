package com.fantasydrawer.ecommerce.backend.service;

import com.fantasydrawer.ecommerce.backend.model.Product;
import com.fantasydrawer.ecommerce.backend.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductService {


    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    public ProductService(ProductRepository productRepository, RestTemplate restTemplate) {
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void syncProductsFromApi() {
        String apiUrl = "https://fakestoreapi.com/products";
        ResponseEntity<Product[]> response = restTemplate.getForEntity(apiUrl, Product[].class);
        Product[] products = response.getBody();

        if (products != null) {
            Arrays.stream(products).forEach(this::saveOrUpdateProduct);
        }
    }

    private void saveOrUpdateProduct(Product apiProduct) {
        Product existing = productRepository.findById(apiProduct.getId()).orElse(null);
        if (existing != null) {
            updateExistingProduct(existing, apiProduct);
        } else {
            productRepository.save(mapToEntity(apiProduct));
        }
    }

    private void updateExistingProduct(Product existing, Product apiProduct) {
        existing.setTitle(apiProduct.getTitle());
        existing.setDescription(apiProduct.getDescription());
        existing.setPrice(apiProduct.getPrice());
        existing.setCategory(apiProduct.getCategory());
        existing.setImageUrl(apiProduct.getImageUrl());

        // Fix the rating setting
        BigDecimal newRating = apiProduct.getRating() != null ? 
            apiProduct.getRating() : 
            BigDecimal.ZERO;
        
        existing.setRating(newRating);
        existing.setUpdatedAt(LocalDateTime.now()); // Assuming you have a setUpdatedAt method
        productRepository.save(existing);
    }

    private Product mapToEntity(Product apiProduct) {
        return new Product(
            apiProduct.getId(),
            apiProduct.getTitle(),
            apiProduct.getDescription(),
            apiProduct.getPrice(),
            apiProduct.getCategory(),
            apiProduct.getImageUrl(),
            apiProduct.getRating() != null ? apiProduct.getRating() : BigDecimal.ZERO
        );
    }
}
