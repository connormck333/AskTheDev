package com.devconnor.askthedev.models;

import lombok.Data;

@Data
public class UserAuthRequest {
    private String email;
    private String password;
    private boolean termsAccepted;
}
