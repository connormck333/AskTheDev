package com.devconnor.askthedev.utils;

import com.devconnor.askthedev.models.ATDSubscription;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.models.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.security.SecureRandom;
import java.util.UUID;

public class Utils {

    public static final String EXCEPTION_PREFIX = "[ATD] ERROR: %s";
    public static final String APPLICATION_JSON = "application/json";
    public static final String SUBSCRIPTION_ID = "subscriptionId";
    public static final String CUSTOMER_ID = "customerId";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String EVENT_ID = "eventId";
    public static final String EVENT_JSON = "eventJson";
    public static final String ACTIVE = "active";
    public static final String INACTIVE = "inactive";

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz"
            + "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String convertToJson(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        return mapper.writeValueAsString(obj);
    }

    public static ATDSubscription createATDSubscription(UUID userId) {
        ATDSubscription atdSubscription = new ATDSubscription();
        atdSubscription.setUserId(userId);
        atdSubscription.setStripeSubscriptionId(SUBSCRIPTION_ID);
        atdSubscription.setType(SubscriptionType.PRO);
        atdSubscription.setStatus(ACTIVE);
        atdSubscription.setActive(true);

        return atdSubscription;
    }

    public static User createUser(UUID userId) {
        User user = new User();
        user.setId(userId);
        user.setCustomerId(CUSTOMER_ID);
        user.setEmail(EMAIL);

        return user;
    }

    public static UserDTO createUserDTO(UUID userId) {
        UserDTO user = new UserDTO();
        user.setEmail(EMAIL);
        user.setCustomerId(CUSTOMER_ID);
        user.setId(userId);

        return user;
    }

    public static String generateRandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            result.append(CHARACTERS.charAt(index));
        }

        return result.toString();
    }
}
