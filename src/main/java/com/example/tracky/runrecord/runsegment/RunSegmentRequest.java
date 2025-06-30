package com.example.tracky.runrecord.runsegment;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runsegment.runcoordinate.RunCoordinate;
import com.example.tracky.runrecord.runsegment.runcoordinate.RunCoordinateRequest;
import com.example.tracky.runrecord.utils.RunRecordUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class RunSegmentRequest {

    @Data
    public static class DTO {
        private LocalDateTime startDate; // "2025-06-22 06:30:00" 형식으로 받아야함
        private LocalDateTime endDate;
        private Integer durationSeconds;
        private Integer distanceMeters;
        private List<RunCoordinateRequest.DTO> coordinates;

        public RunSegment toEntity(RunRecord runRecord) {
            RunSegment runSegment = RunSegment.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .durationSeconds(durationSeconds)
                    .distanceMeters(distanceMeters)
                    .runRecord(runRecord)
                    .pace(RunRecordUtil.calculatePace(distanceMeters, durationSeconds))
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
