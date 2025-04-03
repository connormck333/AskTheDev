package com.devconnor.askthedev.models;

import com.devconnor.askthedev.utils.ModelType;
import com.devconnor.askthedev.utils.ModelTypeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @JsonDeserialize(using = ModelTypeDeserializer.class)
    private ModelType modelType;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private UUID userId;
}
