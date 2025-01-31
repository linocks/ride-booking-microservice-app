package com.ecabs.ride_booking_service.controller;

import com.ecabs.ride_booking_service.dto.RideRequestDto;
import com.ecabs.ride_booking_service.dto.RideResponseDto;
import com.ecabs.ride_booking_service.entity.RideStatus;
import com.ecabs.ride_booking_service.exception.RideNotFoundException;
import com.ecabs.ride_booking_service.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;


@RestController
@Slf4j
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
@Tag(name = "Ride Controller", description = "APIs related to ride requests and status")
public class RideController {

    private final RideService rideService;
    private final RestTemplate restTemplate;

    /**
     * Books a new ride based on the provided passenger and location details.
     *
     * @param rideRequest the details of the ride request
     * @return a ResponseEntity containing a success message if the ride request is successfully published to the RabbitMQ topic/queue
     * @throws Exception if the ride request fails to be published to the RabbitMQ topic
     */
    @PostMapping("booking")
    @Operation(summary = "Book a new ride", description = "Request a new ride based on passenger and location details")
    public Mono<ResponseEntity<String>> bookRide(@Valid @RequestBody RideRequestDto rideRequest) throws Exception {
        return rideService.findNearbyRide(rideRequest)
                .flatMap(success -> {
                    if (success) {
                        log.info("Ride request successfully published");
                        return Mono.just(ResponseEntity.status(HttpStatus.CREATED)
                                .body("Ride request submitted successfully"));
                    } else {
                        log.error("Failed to publish ride request");
                        return Mono.error(new Exception("Failed to publish to rabbitmq topic"));
                    }
                })
                .onErrorResume(Exception.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("An error occurred: " + ex.getMessage()))
                );
    }


    /**
     * Retrieves the status of a ride based on the passenger ID.
     *
     * @param id the passenger ID
     * @return a ResponseEntity containing the ride status
     */
    @GetMapping("/booking/{id}")
    @Operation(summary = "Get the status of a ride", description = "Check the status of a ride by its passenger ID")
    public Mono<ResponseEntity<RideResponseDto>> getRideStatus(@PathVariable String id){
        log.info("Fetching ride status for passengerId: {}", id);
        return rideService.getRideStatus(id)
                .map(response -> {
                    if (RideStatus.NOT_FOUND.equals(response.status())) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(ex -> {
                    log.error("Unexpected error fetching ride status for passengerId: {}", id, ex);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/test-ride-dispatch")
    public String testRideDispatch(){
        return restTemplate.getForObject("http://dispatch-service/api/v1/dispatch", String.class);
    }
}
