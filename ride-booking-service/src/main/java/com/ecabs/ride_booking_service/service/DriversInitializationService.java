package com.ecabs.ride_booking_service.service;

import com.ecabs.ride_booking_service.entity.Driver;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class DriversInitializationService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    ObjectMapper mapper;
    @Value("${ride.request.max-distance}")
    private int maxDistance;

    @PostConstruct
    private void seedDatabase() throws IOException{

        Resource resource = resourceLoader.getResource("classpath:db_data/drivers.json");
        List<Driver> drivers = mongoTemplate.findAll(Driver.class);

        if(drivers.isEmpty()){
            List<Driver> driverList = mapper.readValue(resource.getInputStream(), new TypeReference<List<Driver>>() {
            });
            mongoTemplate.insert(driverList, Driver.class);
        }

    }
}
