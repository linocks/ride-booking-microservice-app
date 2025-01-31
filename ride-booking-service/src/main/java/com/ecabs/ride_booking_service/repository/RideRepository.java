package com.ecabs.ride_booking_service.repository;

import com.ecabs.ride_booking_service.entity.Ride;
import com.ecabs.ride_booking_service.entity.RideStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RideRepository extends ReactiveMongoRepository<Ride, String> {
    Mono<Ride> findByPassengerIdAndStatus(String id, RideStatus status);
}
