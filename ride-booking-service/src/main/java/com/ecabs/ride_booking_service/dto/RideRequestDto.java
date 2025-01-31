package com.ecabs.ride_booking_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record RideRequestDto(
        @NotBlank(message = "Passenger ID must not be null")
        String passengerId,
        @NotNull(message = "Pickup longitude must not be null")
        Double pickupLongitude,
        @NotNull(message = "Pickup latitude must not be null")
        Double pickupLatitude,
        @NotNull(message = "DropOff longitude must not be null")
        Double dropOffLongitude,
        @NotNull(message = "DropOff latitude must not be null")
        Double dropOffLatitude
) implements Serializable {}
