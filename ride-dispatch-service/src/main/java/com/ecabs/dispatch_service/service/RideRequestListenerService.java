package com.ecabs.dispatch_service.service;

import com.ecabs.dispatch_service.dto.RideRequestDto;
import com.ecabs.dispatch_service.entity.Driver;
import com.ecabs.dispatch_service.exception.DriverNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideRequestListenerService {
    private final DriverDispatchService driverDispatchService;

    /**
     * Handles incoming ride requests from the RabbitMQ queue.
     * <p>
     * This method listens to the specified RabbitMQ queue and processes incoming
     * {@link RideRequestDto} messages. It attempts to find a nearby driver and
     * save the ride details. In case of failure to find a driver, an error is logged.
     * </p>
     *
     * @param rideRequest a {@link RideRequestDto} object containing details of the ride request
     * @see DriverDispatchService#findNearbyDriver(RideRequestDto)
     * @see DriverDispatchService#saveRide(Driver, RideRequestDto)
     */
   @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    public void handleRideRequest(RideRequestDto rideRequest) {
        log.info("Searching for nearby ride for request: {} ", rideRequest);

       driverDispatchService.findNearbyDriver(rideRequest)
               .flatMap(driver -> {
                   try {
                       return driverDispatchService.saveRide(driver, rideRequest);
                   } catch (DriverNotFoundException e) {
                       throw new RuntimeException(e);
                   }
                })
               .doOnSuccess(ride -> log.info("Successfully saved ride for passengerId: {}", rideRequest.passengerId()))
               .doOnError(e -> {
                   if (e instanceof DriverNotFoundException) {
                       log.error("Failed to process ride request for passengerId: {}. Reason: {}", rideRequest.passengerId(), e.getMessage());
                   } else {
                       log.error("Unexpected error occurred while processing ride request: {}", e.getMessage());
                   }
               })
               .subscribe();
   }
}

