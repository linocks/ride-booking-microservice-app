package com.ecabs.dispatch_service.controller;

import com.ecabs.dispatch_service.entity.Driver;
import com.ecabs.dispatch_service.service.DriverDispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dispatch")
public class DispatchController {

    @Autowired
    private DriverDispatchService driverDispatchService;

    @GetMapping
    public String getDispatch() {
        return "Dispatch";
    }

    @GetMapping("/drivers")
    public Mono<List<Driver>> getDrivers(){
        return driverDispatchService.getAllDrivers();
    }
}
