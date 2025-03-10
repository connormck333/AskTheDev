package com.devconnor.askthedev.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "PROMPTS")
public class Prompt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String userPrompt;

    @Column(columnDefinition = "TEXT")
    private String pageContent;

    private String webUrl;

    @Column(columnDefinition = "TEXT")
    private String openAIResponse;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private Long userId;
}
