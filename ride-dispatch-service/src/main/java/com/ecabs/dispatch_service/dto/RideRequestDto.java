package com.ecabs.dispatch_service.dto;

import java.io.Serializable;

public record RideRequestDto(
        String passengerId,
        double pickupLongitude,
        double pickupLatitude,
        double dropOffLongitude,
        double dropOffLatitude
) implements Serializable {}
