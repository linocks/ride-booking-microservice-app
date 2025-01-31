package com.ecabs.dispatch_service.repository;

import com.ecabs.dispatch_service.entity.Ride;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends ReactiveMongoRepository<Ride, String> {
}
