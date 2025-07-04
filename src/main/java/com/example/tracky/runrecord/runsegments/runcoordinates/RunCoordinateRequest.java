package com.example.tracky.runrecord.runsegments.runcoordinates;

import lombok.Data;

import java.time.LocalDateTime;

public class RunCoordinateRequest {

    @Data
    public static class DTO {
        private Double lat;
        private Double lon;
        private LocalDateTime recordedAt;
    }

}
