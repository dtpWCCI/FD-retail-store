package com.fantasydrawer.ecommerce.backend.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fantasydrawer.ecommerce.backend.service.StripeService;
import com.stripe.exception.StripeException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private StripeService stripeService;

    /**
     * Request payload example:
     * {
     *   "amount": 2000,
     *   "currency": "usd"
     * }
     */
    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody Map<String, Object> data) {
        Long amount = ((Number) data.get("amount")).longValue();
        String currency = (String) data.get("currency");

        try {
            String clientSecret = stripeService.createPaymentIntent(amount, currency);
            // Return only the client_secret; frontend uses it to confirm the payment
            return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
        } catch (StripeException e) {
            log.error("StripeException while creating PaymentIntent: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
