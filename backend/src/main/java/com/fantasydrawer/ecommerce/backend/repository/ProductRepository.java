package com.fantasydrawer.ecommerce.backend.repository;

import com.fantasydrawer.ecommerce.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    // Find products by category
    List<Product> findByCategory(String category);

    // Find products by price range
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find products by rating above a certain value
    List<Product> findByRatingGreaterThanEqual(BigDecimal rating);

    // Update the version field to handle optimistic locking
    @Modifying
    @Query("UPDATE Product p SET p.version = p.version + 1 WHERE p.id = :id AND p.version = :version")
    int updateVersion(@Param("id") Long id, @Param("version") Long version);

    // Delete products by category
    @Modifying
    @Query("DELETE FROM Product p WHERE p.category = :category")
    void deleteByCategory(@Param("category") String category);

    // Bulk update prices for products by category
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.price = :newPrice WHERE p.category = :category")
    int updatePriceByCategory(@Param("newPrice") BigDecimal newPrice, @Param("category") String category);

    // Batch insert or update products (use with caution for large datasets)
    @Modifying
    @Transactional
    @Query("INSERT INTO Product (id, title, description, price, imageUrl, category, rating, createdAt, updatedAt) " +
            "VALUES (:id, :title, :description, :price, :imageUrl, :category, :rating, :createdAt, :updatedAt)")
    int bulkInsertOrUpdate(
            @Param("id") Long id,
            @Param("title") String title,
            @Param("description") String description,
            @Param("price") BigDecimal price,
            @Param("imageUrl") String imageUrl,
            @Param("category") String category,
            @Param("rating") BigDecimal rating,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("updatedAt") LocalDateTime updatedAt);
}
