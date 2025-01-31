package com.ecabs.ride_booking_service.dto;

import com.ecabs.ride_booking_service.entity.RideStatus;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RideResponseDto(
        String rideId,
        RideStatus status,
        String driverId,
        BigDecimal fare,
        LocalDateTime estimatedArrivalTime,
        GeoJsonPoint driverLocation
) implements Serializable {}
