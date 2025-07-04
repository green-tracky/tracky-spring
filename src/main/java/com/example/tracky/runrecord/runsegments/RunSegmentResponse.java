package com.example.tracky.runrecord.runsegments;

import com.example.tracky._core.utils.JsonUtil;
import com.example.tracky.runrecord.runsegments.runcoordinates.RunCoordinateResponse;
import com.fasterxml.jackson.core.type.TypeReference;
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

            // 좌표 JSON 문자열을 파싱해서 DTO 리스트로 변환
            String coordinateJson = runSegment.getRunCoordinate().getCoordinate();
            this.coordinates = JsonUtil.fromJson(
                    coordinateJson,
                    new TypeReference<List<RunCoordinateResponse.DTO>>() {
                    }
            );
        }
    }
}
