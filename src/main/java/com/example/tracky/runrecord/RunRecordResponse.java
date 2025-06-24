package com.example.tracky.runrecord;

import java.sql.Timestamp;
import java.util.List;

import com.example.tracky.runrecord.picture.PictureResponse;
import com.example.tracky.runrecord.runsegment.RunSegmentResponse;
import com.example.tracky.runrecord.utils.RunRecordUtil;

import lombok.Data;

public class RunRecordResponse {

    /**
     * private Integer id;
     * <p>
     * private String title;
     * <p>
     * private String memo;
     * <p>
     * private Integer calories;
     * <p>
     * private Integer totalDistanceMeters; // 러닝 총 이동거리
     * <p>
     * private Integer totalDurationSeconds; // 러닝 총 시간
     * <p>
     * private Integer avgPace; // 평균 페이스
     * <p>
     * private Integer bestPace; // 최고 페이스. 숫자가 낮아야 함
     * <p>
     * private List<RunSegmentResponse.DTO> segments;
     * <p>
     * private List<PictureResponse.DTO> pictures;
     * <p>
     * private Timestamp createdAt;
     */
    @Data
    public static class SaveDTO {
        private Integer id;
        private String title;
        private String memo;
        private Integer calories;
        private Integer totalDistanceMeters; // 러닝 총 이동거리
        private Integer totalDurationSeconds; // 러닝 총 시간
        private Integer avgPace; // 평균 페이스
        private Integer bestPace; // 최고 페이스. 숫자가 낮아야 함
        private List<RunSegmentResponse.DTO> segments;
        private List<PictureResponse.DTO> pictures;
        private Timestamp createdAt;

        public SaveDTO(RunRecord runRecord) {
            this.id = runRecord.getId();
            this.title = runRecord.getTitle();
            this.memo = runRecord.getMemo();
            this.calories = runRecord.getCalories();
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.createdAt = runRecord.getCreatedAt();
            this.segments = runRecord.getRunSegments().stream()
                    .map(s -> new RunSegmentResponse.DTO(s))
                    .toList();
            this.pictures = runRecord.getPictures().stream()
                    .map(p -> new PictureResponse.DTO(p))
                    .toList();
            this.avgPace = (int) this.segments.stream()
                    .mapToInt(s -> s.getPace())
                    .average()
                    .orElse(0);
            this.bestPace = this.segments.stream()
                    .mapToInt(s -> s.getPace())
                    .min()
                    .orElse(0);
        }

    }

    @Data
    public static class DetailDTO {
        private Integer id;
        private String title;
        private String memo;
        private Integer calories;
        private Integer totalDistanceMeters; // 러닝 총 이동거리
        private Integer totalDurationSeconds; // 러닝 총 시간
        private Integer elapsedTimeInSeconds; // 러닝 총 경과시간
        private Integer avgPace;
        private Integer bestPace;
        private List<RunSegmentResponse.DTO> segments;
        private List<PictureResponse.DTO> pictures;
        private Timestamp createdAt;

        public DetailDTO(RunRecord runRecord) {
            this.id = runRecord.getId();
            this.title = runRecord.getTitle();
            this.memo = runRecord.getMemo();
            this.calories = runRecord.getCalories();
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.elapsedTimeInSeconds = RunRecordUtil.calculateElapsedTimeInSeconds(runRecord.getRunSegments());
            this.createdAt = runRecord.getCreatedAt();
            this.segments = runRecord.getRunSegments().stream()
                    .map(s -> new RunSegmentResponse.DTO(s))
                    .toList();
            this.pictures = runRecord.getPictures().stream()
                    .map(p -> new PictureResponse.DTO(p))
                    .toList();
            this.avgPace = (int) this.segments.stream()
                    .mapToInt(s -> s.getPace())
                    .average()
                    .orElse(0);
            this.bestPace = this.segments.stream()
                    .mapToInt(s -> s.getPace())
                    .min()
                    .orElse(0);
        }

        // public class StartDate {
        // private String startDate; // 예: "오늘", "어제", "2025년 06월 23일"
        // private String startTime; // 예: "14:55"

        // public StartDate(Timestamp timestamp) {
        // // 1. Timestamp를 현재 시스템의 시간대(timezone)를 고려한 ZonedDateTime으로 변환합니다.
        // // 한국에서 실행 중이라면 'Asia/Seoul' 시간대가 적용됩니다.

        // // 2. 날짜와 시간을 각각 원하는 형식으로 포맷팅합니다.
        // this.startDate = formatDateRelatively(zonedDateTime, zoneId);
        // this.startTime = formatTime(zonedDateTime);
        // }

        // }

    }

}
