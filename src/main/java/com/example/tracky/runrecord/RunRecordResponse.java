package com.example.tracky.runrecord;

import com.example.tracky.runrecord.Enum.RunPlaceEnum;
import com.example.tracky.runrecord.picture.PictureResponse;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchv;
import com.example.tracky.runrecord.runsegment.RunSegmentResponse;
import com.example.tracky.runrecord.utils.RunRecordUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class RunRecordResponse {

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
        private LocalDateTime createdAt;
        private Integer userId;
        private List<RunBadgeResponse.DTO> badges;

        public SaveDTO(RunRecord runRecord, List<RunBadgeAchv> awardedBadges) {
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
            this.avgPace = runRecord.getAvgPace();
            this.bestPace = runRecord.getBestPace();
            this.userId = runRecord.getUser().getId();

            // 전달받은 뱃지 획득 엔티티 목록을 DTO 목록으로 변환
            this.badges = awardedBadges.stream()
                    .map(ba -> new RunBadgeResponse.DTO(ba))
                    .toList();
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
        private LocalDateTime createdAt;
        private Integer userId;
        private Integer intensity; // 러닝 강도
        private RunPlaceEnum place; // 러닝 장소

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
            this.avgPace = runRecord.getAvgPace();
            this.bestPace = runRecord.getBestPace();
            this.userId = runRecord.getUser().getId();
            this.intensity = runRecord.getIntensity();
            this.place = runRecord.getPlace();
        }

    }

    @Data
    public static class UpdateDTO {
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
        private LocalDateTime createdAt;
        private Integer userId;
        private Integer intensity; // 러닝 강도
        private RunPlaceEnum place; // 러닝 장소

        public UpdateDTO(RunRecord runRecord) {
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
            this.avgPace = runRecord.getAvgPace();
            this.bestPace = runRecord.getBestPace();
            this.userId = runRecord.getUser().getId();
            this.intensity = runRecord.getIntensity();
            this.place = runRecord.getPlace();
        }
    }

}
