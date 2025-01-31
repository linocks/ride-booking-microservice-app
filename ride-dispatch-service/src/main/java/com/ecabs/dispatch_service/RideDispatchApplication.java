package com.ecabs.dispatch_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableRabbit
@EnableDiscoveryClient
public class RideDispatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(RideDispatchApplication.class, args);
	}

}
