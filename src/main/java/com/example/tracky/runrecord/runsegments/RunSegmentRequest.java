package com.example.tracky.runrecord.runsegments;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runsegments.runcoordinates.Coordinate;
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
        @PastOrPresent(message = "시작 날짜는 현재 또는 과거 시간이어야 합니다")
        private LocalDateTime startDate; // "2025-06-22 06:30:00" 형식으로 받아야함
        @PastOrPresent(message = "종료 날짜는 현재 또는 과거 시간이어야 합니다")
        @NotNull(message = "종료 시간은 필수 입력 항목입니다.")
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

        /**
         * 요청 DTO를 기반으로 RunSegment 엔티티와 그에 속한 RunCoordinate 엔티티를 생성합니다.
         *
         * @param runRecord 부모 엔티티인 RunRecord
         * @return 완전히 조립된 RunSegment 엔티티
         */
        public RunSegment toEntity(RunRecord runRecord) {
            // 1. RunSegment 엔티티를 생성합니다.
            RunSegment runSegment = RunSegment.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .durationSeconds(durationSeconds)
                    .distanceMeters(distanceMeters)
                    .runRecord(runRecord)
                    .pace(RunRecordUtil.calculatePace(distanceMeters, durationSeconds))
                    .build();

            // 2. 요청받은 좌표 DTO 리스트(List<RunCoordinateRequest.DTO>)를
            //    엔티티가 사용할 값 객체 리스트(List<Coordinate>)로 변환합니다.
            List<Coordinate> coordinateVOs = this.coordinates.stream()
                    .map(dto -> dto.toValueObject()) // 각 DTO를 Coordinate 값 객체로 변환
                    .toList();

            // 3. RunCoordinate 엔티티를 생성합니다.
            //    이제 JSON 변환 로직은 사라지고, 변환된 List<Coordinate>를 직접 전달합니다.
            RunCoordinate runCoordinate = RunCoordinate.builder()
                    .coordinates(coordinateVOs)
                    .runSegment(runSegment)
                    .build();

            // 4. RunSegment와 RunCoordinate의 양방향 연관관계를 설정합니다.
            runSegment.setRunCoordinate(runCoordinate);

            return runSegment;
        }
    }
}
