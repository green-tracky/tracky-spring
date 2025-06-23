package com.example.tracky;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HealthController {
    @GetMapping("/")
    public String health() {
        return "<h3>health ok<h3>";
    }
}