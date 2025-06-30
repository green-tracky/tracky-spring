package com.example.tracky.runrecord.runsegment.runcoordinate;

import lombok.Data;

import java.time.LocalDateTime;

public class RunCoordinateResponse {
    
    @Data
    public static class DTO {
        private Integer id;
        private Double lat;
        private Double lon;
        private LocalDateTime createdAt;

        public DTO(RunCoordinate runCoordinate) {
            this.id = runCoordinate.getId();
            this.lat = runCoordinate.getLat();
            this.lon = runCoordinate.getLon();
            this.createdAt = runCoordinate.getCreatedAt();
        }

    }

}
