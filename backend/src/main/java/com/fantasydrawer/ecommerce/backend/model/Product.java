package com.fantasydrawer.ecommerce.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.*;

@Entity
@Table(name = "product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotBlank(message = "Description must not be blank")
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @NotNull(message = "Price must not be null")
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @NotBlank(message = "Category must not be blank")
    private String category;
    
    @NotBlank(message = "Brand must not be blank")
    private String brand;

    @NotNull(message = "Rating must not be null")
    private BigDecimal rating;

    @Column(name = "review_count")
    private int reviewCount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    // Custom constructor
    public Product(@NonNull String title, @NonNull String description,
                @NonNull BigDecimal price, double d, @NonNull String category,
                @NonNull String brand, @NonNull BigDecimal rating, int reviewCount) {
        if (title == null || description == null || category == null || brand == null || rating == null) {
            throw new IllegalArgumentException("None of the fields can be null.");
        }
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.brand = brand; // Assigning brand properly
        this.rating = rating;
        this.reviewCount = reviewCount; // Setting review count
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 0L;
    }

    public void setName(String string) {
        this.title = string;
    }

    public void setPrice(double doubleValue) {
        this.price = BigDecimal.valueOf(doubleValue);
    }

    public void setImage(String string) {
        this.imageUrl = string;
    }

    public void setRating(BigDecimal doubleValue) {
        this.rating = doubleValue;
    }

    public void setReviewCount(int intValue) {
        this.reviewCount = intValue;
    }

    public String getName() {
        return this.title;
    }

    public void setPrice(BigDecimal price2) {
        this.price = price2;
    }

    public String getImage() {
        return this.imageUrl;
    }

    public int getReviewCount() {
        return this.reviewCount; // Return the actual review count
    }
}
