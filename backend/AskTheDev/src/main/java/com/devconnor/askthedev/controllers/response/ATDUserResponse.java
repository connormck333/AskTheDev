package com.devconnor.askthedev.controllers.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ATDUserResponse {
    private String email;
    private Long userId;
    private String message;
}
