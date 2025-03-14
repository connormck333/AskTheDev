package com.devconnor.askthedev.models;

import com.devconnor.askthedev.utils.SubscriptionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "SUBSCRIPTIONS")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private SubscriptionType type;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
