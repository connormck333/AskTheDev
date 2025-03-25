package com.devconnor.askthedev.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "PAYMENTS")
public class ATDPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID userId;
    private String stripeSubscriptionId;
    private double amount;
    private boolean success;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
