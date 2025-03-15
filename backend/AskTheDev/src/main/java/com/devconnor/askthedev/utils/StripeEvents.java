package com.devconnor.askthedev.utils;

public class StripeEvents {
    public static final String CHECKOUT_COMPLETED = "checkout.session.completed";
    public static final String PAYMENT_SUCCESS = "invoice.payment_succeeded";
    public static final String PAYMENT_FAILED = "invoice.payment_failed";
    public static final String SUBSCRIPTION_CREATED = "customer.subscription.created";
    public static final String SUBSCRIPTION_UPDATED = "customer.subscription.updated";
    public static final String SUBSCRIPTION_DELETED = "customer.subscription.deleted";
}
