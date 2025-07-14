package com.example.tracky.runrecord.runsegments;

import com.example.tracky._core.utils.JsonUtil;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runsegments.runcoordinates.RunCoordinate;
import com.example.tracky.runrecord.runsegments.runcoordinates.RunCoordinateRequest;
import com.example.tracky.runrecord.utils.RunRecordUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class RunSegmentRequest {

    @Data
    public static class DTO {
        @NotNull(message = "시작 시간은 필수 입력 항목입니다.")
        @PastOrPresent(message = "시작 시간은 현재 또는 과거의 시간이어야 합니다.")
        private LocalDateTime startDate; // "2025-06-22 06:30:00" 형식으로 받아야함
        @NotNull(message = "종료 시간은 필수 입력 항목입니다.")
        @PastOrPresent(message = "종료 시간은 현재 또는 과거의 시간이어야 합니다.")
        private LocalDateTime endDate;
        @NotNull(message = "운동 시간(초)은 필수 입력 항목입니다.")
        @PositiveOrZero(message = "운동 시간은 0 이상이어야 합니다.")
        private Integer durationSeconds;
        @NotNull(message = "이동 거리(미터)는 필수 입력 항목입니다.")
        @PositiveOrZero(message = "이동 거리는 0 이상이어야 합니다.")
        private Integer distanceMeters;
        @NotEmpty(message = "좌표 데이터는 최소 하나 이상 포함되어야 합니다.")
        @Valid
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
