package com.ecabs.ride_booking_service.controller;

import com.ecabs.ride_booking_service.dto.RideRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RideControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/rides/booking";
    }

    @Test
    public void testBookRide() throws Exception {

        RideRequestDto rideRequestDto = new RideRequestDto(
                "001",
                -74.006,
                40.7128,
                48.8566,
                2.3522
        );

        String requestJson = "{" +
                "\"passengerId\": \"001\", " +
                "\"pickupLatitude\": 40.7128, " +
                "\"pickupLongitude\": -74.006, " +
                "\"dropOffLongitude\": 48.855, " +
                "\"dropOffLatitude\": 2.3522" +
                "}";

        webTestClient.post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Ride request submitted successfully"));
    }

    @Test
    public void testGetRideStatus() throws Exception {
        String passengerId = "001";
        String url = baseUrl + "/" + passengerId;
        webTestClient.get()
                .uri(url)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testGetRideStatusNotFound() throws Exception {
        String passengerId = "12345";
        String url = baseUrl + "/" + passengerId;
        webTestClient.get()
                .uri(url)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }
}