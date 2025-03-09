package com.devconnor.askthedev.models.authentication;

import lombok.Data;

@Data
public class UserAuthRequest {
    private String email;
    private String password;
}
