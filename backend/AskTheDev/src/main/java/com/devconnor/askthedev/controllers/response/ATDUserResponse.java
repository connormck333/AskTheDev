package com.devconnor.askthedev.controllers.response;

import com.devconnor.askthedev.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ATDUserResponse {
    private String email;
    private Long userId;
    private String message;

    public void setUser(User user) {
        this.email = user.getEmail();
        this.userId = user.getId();
    }
}
