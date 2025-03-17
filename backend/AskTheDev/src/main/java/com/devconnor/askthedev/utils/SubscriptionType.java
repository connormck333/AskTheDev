package com.devconnor.askthedev.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionType {
    BASIC(""),
    PRO("");

    private final String value;
}
