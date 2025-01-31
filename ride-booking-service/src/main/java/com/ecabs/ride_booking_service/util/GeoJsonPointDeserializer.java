package com.ecabs.ride_booking_service.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.IOException;

public class GeoJsonPointDeserializer extends JsonDeserializer {
    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        JsonNode coordinates = node.get("coordinates");
        double longitude = coordinates.get(0).asDouble();
        double latitude = coordinates.get(1).asDouble();

        return new GeoJsonPoint(longitude, latitude);
    }
}
