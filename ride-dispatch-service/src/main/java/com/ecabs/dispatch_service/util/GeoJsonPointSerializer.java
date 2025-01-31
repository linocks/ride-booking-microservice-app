package com.ecabs.dispatch_service.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.IOException;

public class GeoJsonPointSerializer extends JsonSerializer<GeoJsonPoint> {
    @Override
    public void serialize(GeoJsonPoint geoJsonPoint, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("type", "Point");
        jsonGenerator.writeArrayFieldStart("coordinates");
        jsonGenerator.writeNumber(geoJsonPoint.getX()); // Longitude
        jsonGenerator.writeNumber(geoJsonPoint.getY()); // Latitude
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
