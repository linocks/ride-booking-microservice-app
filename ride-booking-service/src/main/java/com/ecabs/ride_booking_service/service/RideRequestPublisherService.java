package com.ecabs.ride_booking_service.service;

import com.ecabs.ride_booking_service.config.RabbitMQConfig;
import com.ecabs.ride_booking_service.dto.RideRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideRequestPublisherService {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;

    public void publishRideRequest(RideRequestDto rideRequestDto){
        rabbitTemplate.convertAndSend(rabbitMQConfig.EXCHANGE_NAME, rabbitMQConfig.ROUTING_KEY, rideRequestDto);
    }
}
