package com.fantasydrawer.ecommerce.backend.controller;

import com.fantasydrawer.ecommerce.backend.model.CartItem;
import com.fantasydrawer.ecommerce.backend.repository.CartItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {

    @Autowired
    private CartItemRepository cartItemRepository;

    // GET ALL CART ITEMS
    @GetMapping
    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }

    // GET CART ITEM BY ID
    @GetMapping("/{id}")
    public CartItem getCartItemById(@PathVariable Long id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart Item not found"));
    }

    // CREATE NEW CART ITEM
    @PostMapping
    public CartItem createCartItem(@Valid @RequestBody CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    // UPDATE EXISTING CART ITEM
    @PutMapping("/{id}")
    public CartItem updateCartItem(@PathVariable Long id, @Valid @RequestBody CartItem cartItemUpdate) {
        CartItem existingCartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Cart Item not found"));

        existingCartItem.setProduct(cartItemUpdate.getProduct());
        existingCartItem.setQuantity(cartItemUpdate.getQuantity());
        existingCartItem.setCreatedAt(cartItemUpdate.getCreatedAt());
        existingCartItem.setUpdatedAt(cartItemUpdate.getUpdatedAt());

        return cartItemRepository.save(existingCartItem);
    }

    // DELETE CART ITEM
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        cartItemRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // FIND BY PRODUCT ID
    @GetMapping("/product/{productId}")
    public List<CartItem> findCartItemsByProductId(@PathVariable Long productId) {
        return cartItemRepository.findByProductId(productId);
    }

    // FIND RECENT ITEMS BY PRODUCT WITH MIN QUANTITY
    @GetMapping("/recent")
    public List<CartItem> findRecentCartItems(@RequestParam Long productId, @RequestParam int minQuantity) {
        return cartItemRepository.findRecentItemsByProductWithMinQuantity(productId, minQuantity);
    }
}

