package com.poc.integrationhub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/router-api/2/ping")
    public String ping() {
        return "Hub is alive";
    }
}