package com.ecabs.ride_booking_service;

import com.ecabs.ride_booking_service.util.GeoJsonPointDeserializer;
import com.ecabs.ride_booking_service.util.GeoJsonPointSerializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableCaching
@EnableDiscoveryClient
public class RideBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RideBookingApplication.class, args);
	}

	@Bean
	ObjectMapper objectMapper(){
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addSerializer(GeoJsonPoint.class, new GeoJsonPointSerializer());
		module.addDeserializer(GeoJsonPoint.class, new GeoJsonPointDeserializer());
		mapper.registerModule(module);
		mapper.registerModule(new JavaTimeModule());
		mapper.registerModule(new Jdk8Module());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return mapper;
	}

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
