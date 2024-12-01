package com.fantasydrawer.ecommerce.backend.repository;

import com.fantasydrawer.ecommerce.backend.model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Integer> {
}
