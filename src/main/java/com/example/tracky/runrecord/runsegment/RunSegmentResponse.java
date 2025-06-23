package com.example.tracky.runrecord.runsegment;

import java.sql.Timestamp;
import java.util.List;

import com.example.tracky.runrecord.runsegment.runcoordinate.RunCoordinateResponse;

import lombok.Data;

public class RunSegmentResponse {

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
     * private List<RunCoordinateResponse.DTO> coordinates;
     */
    @Data
    public static class DTO {
        private Integer id;
        private Timestamp startDate; // "2025-06-22 06:30:00"
        private Timestamp endDate;
        private Integer durationSeconds;
        private Integer distanceMeters;
        private Integer calories;
        private Double pace;
        private List<RunCoordinateResponse.DTO> coordinates;

        public DTO(RunSegment runSegment) {
            this.id = runSegment.getId();
            this.startDate = runSegment.getStartDate();
            this.endDate = runSegment.getEndDate();
            this.durationSeconds = runSegment.getDurationSeconds();
            this.distanceMeters = runSegment.getDistanceMeters();
            this.calories = runSegment.getCalories();
            this.pace = runSegment.getPace();
            this.coordinates = runSegment.getRunCoordinates().stream()
                    .map(c -> new RunCoordinateResponse.DTO(c))
                    .toList();
        }

    }

}
