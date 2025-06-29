package com.example.tracky.runrecord.runsegment;

import com.example.tracky.runrecord.runsegment.runcoordinate.RunCoordinateResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class RunSegmentResponse {

    @Data
    public static class DTO {
        private Integer id;
        private LocalDateTime startDate; // "2025-06-22 06:30:00"
        private LocalDateTime endDate;
        private Integer durationSeconds;
        private Integer distanceMeters;
        private Integer pace; // 초단위
        private List<RunCoordinateResponse.DTO> coordinates;

        public DTO(RunSegment runSegment) {
            this.id = runSegment.getId();
            this.startDate = runSegment.getStartDate();
            this.endDate = runSegment.getEndDate();
            this.durationSeconds = runSegment.getDurationSeconds();
            this.distanceMeters = runSegment.getDistanceMeters();
            this.pace = runSegment.getPace();
            this.coordinates = runSegment.getRunCoordinates().stream()
                    .map(c -> new RunCoordinateResponse.DTO(c))
                    .toList();
        }

    }

}
