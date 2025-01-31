package com.ecabs.ride_booking_service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "passengers")
public class Passenger {
    @Id
    private String id;
    private String name;
    private String email;
    private String contactNumber;
    private Float rating;
}
