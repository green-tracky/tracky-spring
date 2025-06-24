package com.example.tracky.runrecord.runsegment.runcoordinate;

import java.sql.Timestamp;

import com.example.tracky.runrecord.runsegment.RunSegment;

import lombok.Data;

public class RunCoordinateRequest {

    /**
     * private Double lat;
     * <p>
     * private Double lon;
     * <p>
     * private Timestamp createdAt;
     */
    @Data
    public static class DTO {
        private Double lat;
        private Double lon;
        private Timestamp createdAt;

        public RunCoordinate toEntity(RunSegment runSegment) {
            return RunCoordinate.builder()
                    .lat(lat)
                    .lon(lon)
                    .createdAt(createdAt)
                    .runSegment(runSegment)
                    .build();
        }

    }

}
