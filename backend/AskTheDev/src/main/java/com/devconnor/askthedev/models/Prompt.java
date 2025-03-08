package com.devconnor.askthedev.models;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "PROMPTS")
public class Prompt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Long userId;

    private String prompt;

    private String response;
}
