package com.ecabs.ride_booking_service.service;

import com.ecabs.ride_booking_service.dto.RideRequestDto;
import com.ecabs.ride_booking_service.dto.RideResponseDto;
import com.ecabs.ride_booking_service.entity.Ride;
import com.ecabs.ride_booking_service.entity.RideStatus;
import com.ecabs.ride_booking_service.repository.RideRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RideServiceTest {

    @InjectMocks
    private RideService rideService;

    @Mock
    private RideRepository rideRepository;

    @Mock
    private RideRequestPublisherService rideRequestPublisherService;

    @Test
    public void testFindNearbyRide() {
        RideRequestDto rideRequestDto = new RideRequestDto(
                "001",
                -74.006,
                40.7128,
                48.8566,
                2.3522
                );

        String passengerId = "001";
        Ride ride = new Ride();
        ride.setPassengerId(passengerId);
        ride.setId("rideId");
        ride.setStatus(RideStatus.MATCHED);
        ride.setDriverId("driverId");
        ride.setFare(BigDecimal.valueOf(100.0));
        ride.setDropOffLocation(new GeoJsonPoint(0.0, 0.0));

        doNothing().when(rideRequestPublisherService).publishRideRequest(rideRequestDto);

        Mono<Boolean> resultMono = rideService.findNearbyRide(rideRequestDto);

        StepVerifier.create(resultMono)
                .expectNext(true)
                .expectComplete()
                .verify();

        verify(rideRequestPublisherService, times(1)).publishRideRequest(rideRequestDto);
    }

    @Test
    public void testGetRideStatusFound() {
        String passengerId = "001";
        Ride ride = new Ride();
        ride.setPassengerId(passengerId);
        ride.setId("rideId");
        ride.setStatus(RideStatus.MATCHED);
        ride.setDriverId("driverId");
        ride.setFare(BigDecimal.valueOf(100.0));
        ride.setDropOffLocation(new GeoJsonPoint(0.0, 0.0));

        when(rideRepository.findByPassengerIdAndStatus(passengerId, RideStatus.MATCHED))
                .thenReturn(Mono.just(ride));


        Mono<RideResponseDto> responseMono = rideService.getRideStatus(passengerId);
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals("rideId", response.rideId());
                    assertEquals(RideStatus.MATCHED, response.status());
                    assertEquals("driverId", response.driverId());
                    assertEquals(BigDecimal.valueOf(100.0), response.fare());
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void testGetRideStatusNotFound() {
        String passengerId = "12345";

        when(rideRepository.findByPassengerIdAndStatus(passengerId, RideStatus.MATCHED)).thenReturn(Mono.empty());

        Mono<RideResponseDto> responseMono = rideService.getRideStatus(passengerId);
        
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals("", response.rideId());
                    assertEquals(RideStatus.NOT_FOUND, response.status());
                    assertEquals("", response.driverId());
                    assertEquals(BigDecimal.valueOf(0.0), response.fare());
                })
                .expectComplete()
                .verify();
    }
}