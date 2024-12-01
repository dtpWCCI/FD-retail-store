package com.fantasydrawer.ecommerce.backend.repository;

import com.fantasydrawer.ecommerce.backend.model.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import jakarta.validation.constraints.NotNull;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Pagination
    @SuppressWarnings("null")
    Page<CartItem> findAll(@NotNull Pageable pageable);

    // Example of a named query
    @Query("SELECT c FROM CartItem c WHERE c.product.id = :productId AND c.quantity > :minQuantity ORDER BY c.createdAt DESC")
    List<CartItem> findRecentItemsByProductWithMinQuantity(
        @Param("productId") Long productId,
        @Param("minQuantity") int minQuantity
    );

    // Find by product ID
    List<CartItem> findByProductId(Long productId);
}