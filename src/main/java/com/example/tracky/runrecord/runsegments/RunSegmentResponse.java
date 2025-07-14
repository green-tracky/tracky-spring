package com.example.tracky.runrecord.runsegments;

import com.example.tracky.runrecord.runsegments.runcoordinates.Coordinate;
import com.example.tracky.runrecord.runsegments.runcoordinates.RunCoordinateResponse;
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

        /**
         * AttributeConverter가 적용된 새로운 엔티티 구조에 맞게 수정한 생성자
         *
         * @param runSegment DB에서 조회한 RunSegment 엔티티
         */
        public DTO(RunSegment runSegment) {
            this.id = runSegment.getId();
            this.startDate = runSegment.getStartDate();
            this.endDate = runSegment.getEndDate();
            this.durationSeconds = runSegment.getDurationSeconds();
            this.distanceMeters = runSegment.getDistanceMeters();
            this.pace = runSegment.getPace();

            // 1. 엔티티에서 List<Coordinate>를 직접 가져옵니다.
            //    JPA와 AttributeConverter 덕분에 이 시점에는 이미 자바 객체 리스트로 변환되어 있습니다.
            List<Coordinate> coordinateVOs = runSegment.getRunCoordinate().getCoordinates();

            // 2. Stream을 사용하여 List<Coordinate>를 List<RunCoordinateResponse.DTO>로 변환합니다.
            //    JsonUtil을 사용한 파싱 로직이 더 이상 필요 없습니다.
            //    map(RunCoordinateResponse.DTO::new)는 각 Coordinate 객체를 DTO의 생성자 인자로 넘겨 새 DTO를 만드는 것과 같습니다.
            this.coordinates = coordinateVOs.stream()
                    .map(coordinate -> new RunCoordinateResponse.DTO(coordinate))
                    .toList();
        }
    }
}
