package com.example.tracky.runrecord.runsegments.runcoordinates;

import lombok.Data;

import java.time.LocalDateTime;

public class RunCoordinateResponse {

    @Data
    public static class DTO {
        private Double lat;
        private Double lon;
        private LocalDateTime recordedAt;

        /**
         * Coordinate 값 객체를 응답용 DTO로 변환하는 생성자입니다.
         *
         * @param coordinate 엔티티 내부에 포함된 Coordinate 값 객체
         */
        public DTO(Coordinate coordinate) {
            this.lat = coordinate.getLat();
            this.lon = coordinate.getLon();
            this.recordedAt = coordinate.getRecordedAt();
        }
    }

}
