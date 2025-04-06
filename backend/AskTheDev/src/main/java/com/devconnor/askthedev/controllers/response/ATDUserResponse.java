package com.devconnor.askthedev.controllers.response;

import com.devconnor.askthedev.models.UserDTO;
import com.devconnor.askthedev.utils.SubscriptionType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ATDUserResponse extends ATDResponse {
    private String email;
    private UUID userId;
    private String authToken;

    private boolean activeSubscription;
    private SubscriptionType subscriptionType;

    public void setUser(UserDTO user) {
        this.email = user.getEmail();
        this.userId = user.getId();
    }
}
