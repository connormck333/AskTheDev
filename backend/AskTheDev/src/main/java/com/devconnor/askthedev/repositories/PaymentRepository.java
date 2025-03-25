package com.devconnor.askthedev.repositories;

import com.devconnor.askthedev.models.ATDPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<ATDPayment, String> {
}
