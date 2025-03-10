package com.devconnor.askthedev.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(columnDefinition = "TEXT")
    private String openAIResponse;

    private Long userId;
}
