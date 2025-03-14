package com.devconnor.askthedev.controllers.response;

import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.utils.SubscriptionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ATDUserResponse extends ATDResponse {
    private String email;
    private Long userId;

    private boolean activeSubscription;
    private SubscriptionType subscriptionType;

    public void setUser(User user) {
        this.email = user.getEmail();
        this.userId = user.getId();
    }
}
