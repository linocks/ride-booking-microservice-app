package com.ecabs.ride_booking_service.service;

import com.ecabs.ride_booking_service.dto.RideRequestDto;
import com.ecabs.ride_booking_service.dto.RideResponseDto;
import com.ecabs.ride_booking_service.entity.RideStatus;
import com.ecabs.ride_booking_service.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideService {
    private final RideRepository rideRepository;
    private final RideRequestPublisherService rideRequestPublisherService;


    /**
     * This method publishes a ride request and logs the action.
     *
     * This method takes a {@link RideRequestDto} object and publishes it to a RabbitMQ topic/queue using
     * the {@link RideRequestPublisherService}.
     * The method logs the ride request and returns a boolean indicating the success of the publication.
     *
     * @param rideRequestDto The ride request details including pickup and drop-off locations.
     * @return {@code true} indicating that the ride request was successfully published.
     */
    public Mono<Boolean> findNearbyRide(RideRequestDto rideRequestDto){
        log.info("Publishing ride request: {}", rideRequestDto);
        return Mono.fromCallable(() -> {
            try {
                rideRequestPublisherService.publishRideRequest(rideRequestDto);
                return true;
            } catch (Exception e) {
                log.error("Failed to publish ride request: {}", e.getMessage());
                throw e;
            }
        });
    }


    /**
     * This method retrieves the ride status for a specific passenger from the mongoDB database.
     *
     * The method fetches the ride status for a given passenger ID from the repository, filtering by rides with the
     * status {@link RideStatus#MATCHED}. The result is cached using Spring's cache abstraction to optimize performance.
     * If a matched ride is found, a {@link RideResponseDto} is returned with the ride details. If no matched ride is found,
     * a {@link RideResponseDto} with a status of {@link RideStatus#NOT_FOUND} is returned.
     *
     * @param passengerId The ID of the passenger for whom the ride status is being queried.
     * @return A {@link RideResponseDto} containing the details of the ride, or a response indicating that the ride was not found.
     */
    @Cacheable(value = "rideStatus", key = "#passengerId", unless = "#result.status == T(com.ecabs.ride_booking_service.entity.RideStatus).NOT_FOUND")
    public Mono<RideResponseDto> getRideStatus(String passengerId) {
        log.info("Reading ride status from database for passengerId: {}", passengerId);

        return rideRepository.findByPassengerIdAndStatus(passengerId, RideStatus.MATCHED)
                .map(matchedRide -> new RideResponseDto(
                        matchedRide.getId(),
                        matchedRide.getStatus(),
                        matchedRide.getDriverId(),
                        matchedRide.getFare(),
                        LocalDateTime.now().plusMinutes(5),
                        new GeoJsonPoint(matchedRide.getDropOffLocation())
                ))
                .switchIfEmpty(Mono.just(new RideResponseDto(
                        "",
                        RideStatus.NOT_FOUND,
                        "",
                        BigDecimal.valueOf(0.0),
                        LocalDateTime.now(),
                        new GeoJsonPoint(0.0, 0.0)
                )));
    }

}
