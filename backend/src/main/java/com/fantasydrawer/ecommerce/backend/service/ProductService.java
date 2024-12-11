package com.fantasydrawer.ecommerce.backend.service;
import java.util.List;

import com.fantasydrawer.ecommerce.backend.model.Product;


public interface ProductService {
    Product getProductById(Long id);
    boolean deleteProductById(Long id);
    Product saveProduct(Product product);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    List<Product> getAllProducts();
    void fetchAndSaveProducts();
}
