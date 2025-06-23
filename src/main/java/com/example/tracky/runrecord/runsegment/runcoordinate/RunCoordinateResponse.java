package com.example.tracky.runrecord.runsegment.runcoordinate;

import java.sql.Timestamp;

import lombok.Data;

public class RunCoordinateResponse {

    /**
     * private Double lat;
     * <p>
     * private Double lon;
     * <p>
     * private Timestamp createdAt;
     */
    @Data
    public static class DTO {
        private Integer id;
        private Double lat;
        private Double lon;
        private Timestamp createdAt;

        public DTO(RunCoordinate runCoordinate) {
            this.id = runCoordinate.getId();
            this.lat = runCoordinate.getLat();
            this.lon = runCoordinate.getLon();
            this.createdAt = runCoordinate.getCreatedAt();
        }

    }

}
