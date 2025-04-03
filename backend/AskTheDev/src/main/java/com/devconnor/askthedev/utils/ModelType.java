package com.devconnor.askthedev.utils;

import com.devconnor.askthedev.exception.InvalidModelTypeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModelType {

    GPT4O_MINI("gpt-4o-mini"),
    GPT4O("gpt-4o"),
    OPENAI_O3_MINI("openai-o3-mini");

    private final String value;

    public static ModelType fromString(String value) {
        for (ModelType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }

        throw new InvalidModelTypeException(value);
    }
}
