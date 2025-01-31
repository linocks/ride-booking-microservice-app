package com.ecabs.dispatch_service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "rides")
public class Ride {
    @Id
    private String id;

    private String passengerId;
    private String driverId;
    private RideStatus status;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint pickupLocation;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint dropOffLocation;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint driverLocation;

    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
    private BigDecimal fare;

    public Ride(String passengerId, String driverId, GeoJsonPoint pickupLocation, GeoJsonPoint dropOffLocation, GeoJsonPoint driverLocation, LocalDateTime requestedAt, BigDecimal fare){
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.pickupLocation = pickupLocation;
        this.dropOffLocation = dropOffLocation;
        this.driverLocation = driverLocation;
        this.requestedAt = requestedAt;
        this.fare = fare;
        this.status = RideStatus.MATCHED;
    }
}
