package com.example.tracky.runrecord.runsegment.runcoordinate;

import com.example.tracky.runrecord.runsegment.RunSegment;
import lombok.Data;

import java.time.LocalDateTime;

public class RunCoordinateRequest {

    @Data
    public static class DTO {
        private Double lat;
        private Double lon;
        private LocalDateTime createdAt;

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
