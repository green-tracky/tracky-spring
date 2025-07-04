package com.example.tracky.runrecord.runsegments;

import com.example.tracky._core.utils.JsonUtil;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runsegments.runcoordinates.RunCoordinate;
import com.example.tracky.runrecord.runsegments.runcoordinates.RunCoordinateRequest;
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

            // 좌표 리스트를 JSON 문자열로 변환
            String coordinateJson = JsonUtil.toJson(coordinates);

            // RunCoordinate 엔티티 생성(구간별 1:1)
            RunCoordinate runCoordinate = RunCoordinate.builder()
                    .coordinate(coordinateJson)
                    .runSegment(runSegment)
                    .build();

            // 연관관계 세팅 (구간 <-> 좌표)
            runSegment.setRunCoordinate(runCoordinate);

            return runSegment;
        }
    }
}
