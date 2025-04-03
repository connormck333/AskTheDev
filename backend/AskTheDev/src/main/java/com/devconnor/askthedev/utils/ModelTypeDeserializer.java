package com.devconnor.askthedev.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class ModelTypeDeserializer extends JsonDeserializer<ModelType> {
    @Override
    public ModelType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return ModelType.fromString(p.getValueAsString());
    }
}
