package com.ecabs.dispatch_service.service;

import com.ecabs.dispatch_service.dto.RideRequestDto;
import com.ecabs.dispatch_service.entity.Driver;
import com.ecabs.dispatch_service.entity.Ride;
import com.ecabs.dispatch_service.exception.DriverNotFoundException;
import com.ecabs.dispatch_service.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class DriverDispatchService {
    private final RideRepository rideRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Value("${ride.request.max-distance}")
    private double MAX_DISTANCE;


    /**
     * Finds nearby driver based on the provided ride request details.
     * <p>
     * This method uses the pickup location from the provided {@link RideRequestDto} to query the MongoDB database
     * for drivers within a specified distance. The query is constructed using the {@link GeoJsonPoint} for spatial
     * queries, and the maximum distance is defined by the {@code MAX_DISTANCE} property.
     *
     * @param requestDto The ride request details including pickup and drop off location coordinates.
     * @return {@link Driver} object that is within the specified distance from the pickup location.
     */
    public Mono<Driver> findNearbyDriver(RideRequestDto requestDto) {
        GeoJsonPoint pickupLocation = new GeoJsonPoint(requestDto.pickupLongitude(), requestDto.pickupLatitude());
        Query query = new Query();
        query.addCriteria(Criteria.where("location").nearSphere(pickupLocation).maxDistance(MAX_DISTANCE));

        log.info("Finding nearby drivers for pickup location: {}", pickupLocation);
        return reactiveMongoTemplate.findOne(query, Driver.class);
    }


    /**
     * Saves ride details for the provided for a particular request.
     *
     * This method takes a driver and the ride request details. It checks if the driver is null
     * and throws a {@link DriverNotFoundException} if no driver is found. It creates a {@link Ride}
     * object using the provided ride request details and saves the ride in the repository.
     *
     * @param driver    The {@link Driver} object that is available for the ride.
     * @param requestDto The ride request details including pickup and drop-off locations.
     * @throws DriverNotFoundException If the list of drivers is empty or null.
     */
    public Mono<Void> saveRide(Driver driver, RideRequestDto requestDto) throws DriverNotFoundException {
        if (driver == null) {
            log.warn("No drivers found for passengerId: {}", requestDto.passengerId());
            throw new DriverNotFoundException("No Drivers Found");
        }

        Ride ride = createRide(requestDto, driver);

        log.info("Saving ride for passengerId: {}", requestDto.passengerId());
        return rideRepository.insert(ride).then();
    }


    /**
     * Creates a {@link Ride} object based on the provided ride request and driver details.
     *
     * This method constructs a {@link Ride} object using the pickup and drop-off locations from the ride request
     * and the driver's location. It also calculates the fare and arrival time for the ride. The created ride object
     * includes the passenger ID, driver ID, and various location details.
     *
     * @param requestDto The ride request details including pickup and drop-off locations.
     * @param driver    The {@link Driver} assigned to the passenger for that ride or trip.
     * @return A {@link Ride} object containing the details of the ride.
     */
    private Ride createRide(RideRequestDto requestDto, Driver driver) {
        var pickupLocation = new GeoJsonPoint(requestDto.pickupLongitude(), requestDto.pickupLatitude());
        var dropOffLocation = new GeoJsonPoint(requestDto.dropOffLongitude(), requestDto.dropOffLatitude());

        BigDecimal fare = calculateFare(pickupLocation, dropOffLocation);
        LocalDateTime arrivalTime = calculateArrivalTime(driver.getLocation(), pickupLocation);
        Ride ride = new Ride(
                requestDto.passengerId(),
                driver.getId(),
                pickupLocation,
                dropOffLocation,
                driver.getLocation(),
                arrivalTime,
                fare);

        log.debug("Created ride for driverId: {}, passengerId: {}", driver.getId(), requestDto.passengerId());
        return ride;
    }

    public BigDecimal calculateFare(GeoJsonPoint pickupLocation, GeoJsonPoint dropOffLocation) {
        return BigDecimal.valueOf(Math.round((Math.random() * (100.0 - 1.0)) + 1.0));
    }

    public LocalDateTime calculateArrivalTime(GeoJsonPoint pickupLocation, GeoJsonPoint dropOffLocation) {
        Random random = new Random();
        int max = 10;
        int randomMinutes = random.nextInt(max) + 1;
        return LocalDateTime.now().plusMinutes(randomMinutes);
    }

    public Mono<List<Driver>> getAllDrivers(){
        return reactiveMongoTemplate.findAll(Driver.class).collectList();
    }

}
