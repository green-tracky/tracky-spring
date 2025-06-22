package com.example.tracky.runrecord.runsegment;

import java.sql.Timestamp;
import java.util.List;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runsegment.runcoordinate.RunCoordinate;
import com.example.tracky.runrecord.runsegment.runcoordinate.RunCoordinateRequest;

import lombok.Data;

public class RunSegmentRequest {

    /**
     * private Timestamp startDate; // "2025-06-22 06:30:00" 형식으로 받아야함
     * <p>
     * private Timestamp endDate;
     * <p>
     * private Integer durationSeconds;
     * <p>
     * private Integer distanceMeters;
     * <p>
     * private Integer calories;
     * <p>
     * private Double pace;
     * <p>
     * private List<RunCoordinateRequest.DTO> coordinates;
     */
    @Data
    public static class DTO {
        private Timestamp startDate; // "2025-06-22 06:30:00" 형식으로 받아야함
        private Timestamp endDate;
        private Integer durationSeconds;
        private Integer distanceMeters;
        private Integer calories;
        private Double pace;
        private List<RunCoordinateRequest.DTO> coordinates;

        public RunSegment toEntity(RunRecord runRecord) {
            RunSegment runSegment = RunSegment.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .durationSeconds(durationSeconds)
                    .distanceMeters(distanceMeters)
                    .calories(calories)
                    .pace(pace)
                    .runRecord(runRecord)
                    .build();

            // 좌표 변환
            List<RunCoordinate> runCoordinates = coordinates.stream()
                    .map(c -> c.toEntity(runSegment))
                    .toList();
            runSegment.getRunCoordinates().addAll(runCoordinates);

            return runSegment;
        }
    }

}
