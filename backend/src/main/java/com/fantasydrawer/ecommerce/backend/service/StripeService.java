// backend/src/main/java/com/fantasydrawer/ecommerce/backend/service/StripeService.java
package com.fantasydrawer.ecommerce.backend.service;


import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Service
public class StripeService {

    /**
     * Create a Stripe PaymentIntent for the given amount (in cents) and currency.
     * Returns the client_secret so the frontend can confirm payment.
     */
    public String createPaymentIntent(Long amountInCents, String currency) throws StripeException {
        PaymentIntentCreateParams params =
          PaymentIntentCreateParams.builder()
            .setAmount(amountInCents)
            .setCurrency(currency)
            // Optional: if you already have a customer ID, attach it:
            // .setCustomer(customerId)
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                  .setEnabled(true)
                  .build()
            )
            .build();

        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getClientSecret();
    }
}
