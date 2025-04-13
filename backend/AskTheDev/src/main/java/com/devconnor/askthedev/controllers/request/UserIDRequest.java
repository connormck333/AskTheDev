package com.devconnor.askthedev.controllers.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserIDRequest {
    private UUID userId;
}
