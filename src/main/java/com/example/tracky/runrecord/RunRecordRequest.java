package com.example.tracky.runrecord;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

public class RunRecordRequest {

    @Data
    public static class DTO {
        private String title;
        private String memo;
        private List<Segment> segments;

        @Data
        class Segment {
            private Timestamp startDate; // "2025-06-22 06:30:00" 형식으로 받아야함
            private Timestamp endDate;
            private Integer durationSeconds;
            private Integer distanceMeters;
            private List<Coordinate> coordinates;

            @Data
            class Coordinate {
                private Double lat;
                private Double lon;
                private Timestamp createdAt;
            }
        }
    }
}
