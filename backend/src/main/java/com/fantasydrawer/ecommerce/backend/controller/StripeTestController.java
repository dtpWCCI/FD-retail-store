package com.fantasydrawer.ecommerce.backend.controller;

import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripeTestController {

    /**
     * Hitting GET /api/stripe-test will create a temporary test Customer
     * in your Stripe dashboard. If you see a 200 OK and a customer ID in the response,
     * Stripe is wired up correctly.
     */
    @GetMapping("/api/stripe-test")
    public String createTestCustomer() throws Exception {
        // Build a minimal “create customer” request:
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail("test@example.com")
                .build();

        // This line actually calls Stripe’s API:
        Customer customer = Customer.create(params);

        return "✅ Created test customer: " + customer.getId();
    }
}
