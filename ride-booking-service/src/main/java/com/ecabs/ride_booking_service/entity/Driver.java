package com.ecabs.ride_booking_service.entity;

import com.ecabs.ride_booking_service.util.GeoJsonPointDeserializer;
import com.ecabs.ride_booking_service.util.GeoJsonPointSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "drivers")
@Data
@NoArgsConstructor
public class Driver {
    @Id
    private String id;
    private String name;
    private Vehicle vehicle;
    @JsonSerialize(using = GeoJsonPointSerializer.class)
    @JsonDeserialize(using = GeoJsonPointDeserializer.class)
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;
    private String status;
    private double rating;
    private int totalRides;
    @Field("createdAt")
    private LocalDateTime lastOnlineAt;
    @Field("lastOnlineAt")
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vehicle {
        private String make;
        private String model;
        private String licensePlate;
    }

}
