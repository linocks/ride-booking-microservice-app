package com.ecabs.dispatch_service.service;

import com.ecabs.dispatch_service.dto.RideRequestDto;
import com.ecabs.dispatch_service.entity.Driver;
import com.ecabs.dispatch_service.entity.Ride;
import com.ecabs.dispatch_service.exception.DriverNotFoundException;
import com.ecabs.dispatch_service.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverDispatchServiceTest {

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;
    @InjectMocks
    DriverDispatchService driverDispatchService;
    @Mock
    RideRepository rideRepository;

    @Value("${ride.request.max-distance}")
    int MAX_DISTANCE;

    private Driver expectedDriver;
    RideRequestDto requestDto;

    @BeforeEach
    void setup() {
        requestDto = new RideRequestDto("passengerId", -74.006, 40.7128, -73.935242, 40.730610);

        expectedDriver = new Driver();
        expectedDriver.setId("driverId");
        expectedDriver.setLocation(new GeoJsonPoint(-73.006, 38.7122));
        expectedDriver.setRating(4.6);
        expectedDriver.setVehicle(new Driver.Vehicle("Ford", "F150", "GB 3432-18"));
    }


    @Test
    void findNearbyDriver() {
        GeoJsonPoint pickupLocation = new GeoJsonPoint(requestDto.pickupLongitude(), requestDto.pickupLatitude());

        when(reactiveMongoTemplate.findOne(any(Query.class), eq(Driver.class)))
                .thenReturn(Mono.just(expectedDriver));

        Mono<Driver> resultMono = driverDispatchService.findNearbyDriver(requestDto);

        StepVerifier.create(resultMono)
                .expectNext(expectedDriver)
                .expectComplete()
                .verify();
    }

    @Test
    void saveRide() throws DriverNotFoundException {

        Ride ride = new Ride(
                "passengerId",
                expectedDriver.getId(),
                new GeoJsonPoint(requestDto.pickupLongitude(), requestDto.pickupLatitude()),
                new GeoJsonPoint(requestDto.dropOffLongitude(), requestDto.dropOffLatitude()),
                expectedDriver.getLocation(),
                LocalDateTime.now(),
                BigDecimal.valueOf(50.0)
        );

        when(rideRepository.insert(any(Ride.class))).thenReturn(Mono.just(ride)); // Adapted to return Mono

        StepVerifier.create(driverDispatchService.saveRide(expectedDriver, requestDto))
                .expectComplete()
                .verify();

        verify(rideRepository, times(1)).insert(any(Ride.class));
    }

}