package com.example.tracky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TrackyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrackyApplication.class, args);
    }

}
