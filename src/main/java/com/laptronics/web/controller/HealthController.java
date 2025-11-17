package com.laptronics.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/status")
    public Map<String, String> getStatus() {
        return Map.of(
                "status", "running",
                "service", "users"
        );
    }

}
