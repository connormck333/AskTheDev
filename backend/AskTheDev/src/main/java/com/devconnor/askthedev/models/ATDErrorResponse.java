package com.devconnor.askthedev.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ATDErrorResponse {
    private int statusCode;
    private String message;
}
