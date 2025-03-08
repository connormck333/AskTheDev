package com.devconnor.askthedev.models;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;
}
