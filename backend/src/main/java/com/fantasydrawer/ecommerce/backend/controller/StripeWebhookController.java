package com.fantasydrawer.ecommerce.backend.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
        @RequestHeader("Stripe-Signature") String sigHeader) {
        log.info("Received Stripe webhook. Signature header: {}", sigHeader);

        try {
            // Verify signature and construct the event
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            // Log the event type
            log.info("Verified event type: {}", event.getType());

            // Handle the event (example for payment_intent.succeeded)
            if ("payment_intent.succeeded".equals(event.getType())) {
                log.info("PaymentIntent succeeded! 🎉");
                // Add your logic here (e.g., mark order as paid)
            } else {
                log.info("Unhandled event type: {}", event.getType());
            }

            return ResponseEntity.ok("Received and processed webhook event: " + event.getType());
        } catch (SignatureVerificationException e) {
            log.error("⚠️ Signature verification failed!", e);
            return ResponseEntity.badRequest().body("Signature verification failed");
        } catch (Exception e) {
            log.error("⚠️ Unexpected error while handling webhook", e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}
