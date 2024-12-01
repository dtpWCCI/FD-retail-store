package com.fantasydrawer.ecommerce.backend.service.impl;

import com.fantasydrawer.ecommerce.backend.model.Product;
import com.fantasydrawer.ecommerce.backend.repository.ProductRepository;
import com.fantasydrawer.ecommerce.backend.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    private static final String FAKE_STORE_API_URL = "https://fakestoreapi.com/products";

    @Override
    public Product getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }

    @Override
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Product createProduct(Product product) {
        product.setVersion(0L); // Initialize version for new products
        return productRepository.save(product);
    }

    @Override
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public Product updateProduct(Long id, Product product) {
        Optional<Product> existingProduct = productRepository.findById(id);
        
        if (existingProduct.isPresent()) {
            Product currentProduct = existingProduct.get();
            
            try {
                // Copy fields from incoming product to currentProduct
                currentProduct.setName(product.getName());
                currentProduct.setDescription(product.getDescription());
                currentProduct.setPrice(product.getPrice());
                currentProduct.setCategory(product.getCategory());
                currentProduct.setImage(product.getImage());
                currentProduct.setRating(product.getRating());
                currentProduct.setReviewCount(product.getReviewCount());

                return productRepository.save(currentProduct);
            } catch (ObjectOptimisticLockingFailureException e) {
                // This exception will trigger a retry
                throw e;
            }
        }
        
        return null;
    }

    @Recover
    public Product recoverFromException(ObjectOptimisticLockingFailureException e, Long id, Product product) {
        // Log the exception or implement your recovery strategy
        System.out.println("Failed to update product: " + e.getMessage());
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 500))
public void fetchAndSaveProducts() {
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
        FAKE_STORE_API_URL,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Map<String, Object>>>() {}
    );

    List<Map<String, Object>> apiProducts = response.getBody();

    if (apiProducts != null) {
        for (Map<String, Object> apiProduct : apiProducts) {
            Product product = mapApiResponseToProduct(apiProduct);

            // Fetch the existing product (if any)
            Optional<Product> existingProductOpt = productRepository.findById(product.getId());
            if (existingProductOpt.isPresent()) {
                Product existingProduct = existingProductOpt.get();

                // Update fields
                existingProduct.setName(product.getName());
                existingProduct.setDescription(product.getDescription());
                existingProduct.setPrice(product.getPrice());
                existingProduct.setCategory(product.getCategory());
                existingProduct.setImage(product.getImage());
                existingProduct.setRating(product.getRating());
                existingProduct.setReviewCount(product.getReviewCount());

                try {
                    productRepository.save(existingProduct);
                } catch (ObjectOptimisticLockingFailureException e) {
                    // Log the exception
                    log.warn("Optimistic lock failure while updating product {}: {}", product.getId(), e.getMessage());
                    
                    // Retry once after a short delay
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    
                    // Try saving again
                    productRepository.save(existingProduct);
                }
            } else {
                product.setVersion(0L); // Initialize version for new products
                try {
                    productRepository.save(product);
                } catch (ObjectOptimisticLockingFailureException e) {
                    // Log the exception
                    log.warn("Optimistic lock failure while creating product {}: {}", product.getId(), e.getMessage());
                    
                    // Retry once after a short delay
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    
                    // Try saving again
                    productRepository.save(product);
                }
            }
        }
    }
}

private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    // Map API response to Product entity
    private Product mapApiResponseToProduct(Map<String, Object> apiProduct) {
        Product product = new Product();
        product.setId(((Number) apiProduct.get("id")).longValue());
        product.setName((String) apiProduct.get("title"));
        product.setDescription((String) apiProduct.get("description"));
        product.setPrice(((Number) apiProduct.get("price")).doubleValue());
        product.setCategory((String) apiProduct.get("category"));
        product.setImage((String) apiProduct.get("image"));

        // Handle nested rating object
        Object ratingObj = apiProduct.get("rating");
        if (ratingObj instanceof Map) {
            Map<?, ?> rating = (Map<?, ?>) ratingObj;
            product.setRating(((Number) rating.get("rate")).doubleValue());
            product.setReviewCount(((Number) rating.get("count")).intValue());
        }

        return product;
    }
}
