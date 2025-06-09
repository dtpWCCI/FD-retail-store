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
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
@EnableRetry
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    private static final String FAKE_STORE_API_URL = "https://fakestoreapi.com/products";

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    public boolean deleteProductById(Long id) {
        try {
            productRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("Error deleting product with id: {} - {}", id, e.getMessage());
            return false;
        }
    }

    @Override
    public Product createProduct(Product product) {
        product.setVersion(0L); // Initialize version for new products
        return saveProduct(product);
    }

    @Override
    @Transactional
    @Retryable(
            value = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        return saveProduct(existingProduct);
    }
    
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    @Retryable(
            value = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    
    @Override
    public void fetchAndSaveProducts() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Map<String, Object>>> response;
        try {
            response = restTemplate.exchange(
                    FAKE_STORE_API_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
        } catch (Exception e) {
            log.error("Error fetching products from API: {}", e.getMessage());
            return; // Early exit in case of an API failure
        }

        List<Map<String, Object>> apiProducts = response.getBody();

        if (apiProducts != null) {
            for (Map<String, Object> apiProduct : apiProducts) {
                Product product = mapApiResponseToProduct(apiProduct);

                if (product == null) {
                    log.error("Skipping product with invalid data: {}", apiProduct);
                    continue; // Skip invalid product
                }

                try {
                    Optional<Product> existingProductOpt = productRepository.findById(product.getId());
                    if (existingProductOpt.isPresent()) {
                        Product existingProduct = existingProductOpt.get();
                        existingProduct.setName(product.getName());
                        existingProduct.setDescription(product.getDescription());
                        existingProduct.setPrice(product.getPrice());
                        existingProduct.setCategory(product.getCategory());
                        existingProduct.setImage(product.getImage());
                        existingProduct.setRating(product.getRating());
                        existingProduct.setReviewCount(product.getReviewCount());
                        productRepository.save(existingProduct);
                    } else {
                        product.setVersion(0L); // Initialize version for new products
                        productRepository.save(product);
                    }
                } catch (ObjectOptimisticLockingFailureException e) {
                    log.warn("Optimistic lock failure while processing product {}: {}", product.getId(), e.getMessage());
                    throw new RuntimeException("Product was updated or deleted by another transaction", e);
                }
            }
        }
    }

    private Product mapApiResponseToProduct(Map<String, Object> apiProduct) {
        Product product = Product.builder()
                .title("Default Title")
                .description("Default Description")
                .price(BigDecimal.valueOf(0.0))
                .category("Default Category")
                .rating(BigDecimal.valueOf(0.0))
                .build();

                String title = (String) apiProduct.get("title");
                if (title != null && !title.isEmpty()) {
                    product.setName(title);
                }
        
                product.setId(((Number) apiProduct.get("id")).longValue());
                product.setDescription((String) apiProduct.get("description"));
                product.setPrice(((Number) apiProduct.get("price")).doubleValue());
                product.setCategory((String) apiProduct.get("category"));
                product.setImage((String) apiProduct.get("image"));

        Object ratingObj = apiProduct.get("rating");
        if (ratingObj instanceof Map) {
            Map<?, ?> rating = (Map<?, ?>) ratingObj;
            product.setRating(BigDecimal.valueOf(((Number) rating.get("rate")).doubleValue()));
            product.setReviewCount(((Number) rating.get("count")).intValue());
        }

        return product;
    }

    @Recover
    public Product recoverFromException(ObjectOptimisticLockingFailureException e, Long id, Product product) {
        log.warn("Failed to update product: {}", e.getMessage());
        return null;
    }

    @Recover
    public void recoverFromFetchFailure(ObjectOptimisticLockingFailureException e) {
    log.error("Fetch and save products failed after retries: {}", e.getMessage());
    }

}