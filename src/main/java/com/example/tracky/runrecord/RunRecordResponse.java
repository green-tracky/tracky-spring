package com.example.tracky.runrecord;

import java.util.List;

import com.example.tracky.runrecord.picture.PictureResponse;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import com.example.tracky.runrecord.runsegment.RunSegmentResponse;

import lombok.Data;

public class RunRecordResponse {
    /**
     * private Integer totalDistanceMeters;
     * <p>
     * private Integer totalDurationSeconds;
     * <p>
     * private Integer countRecode;
     * <p>
     * private String avgPace;
     * <p>
     * private List<DTO> runRecords;
     */
    @Data
    public static class MainPageDTO {
        private Integer totalDistanceMeters; // 총 거리. 미터 단위
        private Integer totalDurationSeconds; // 총 시간. 초 단위
        private Integer countRecode;
        private Integer avgPace;
        private List<DTO> recentRuns; // TODO : 최근활동에 맞는 영어이름으로 변경
        private List<RunBadgeResponse.DTO> badges; // TODO : 나중에 획득한 뱃지 들어넣기
        // private String level; // TODO : 나중에 레벨 입력

        public MainPageDTO(RunRecord runRecord, Integer avgPace, List<RunBadgeResponse.DTO> badges, /* String level */
                List<DTO> recentRuns) {
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.countRecode = recentRuns.size();
            this.avgPace = avgPace;
            this.badges = badges;
            // this.level = level;
            this.recentRuns = recentRuns;
        }

    }

    /**
     * private Integer id;
     * <p>
     * private String title;
     * <p>
     * private String memo;
     * <p>
     * private Integer calories;
     * <p>
     * private List<RunSegmentResponse.DTO> segments;
     * <p>
     * private List<PictureResponse.DTO> pictures;
     */
    @Data
    public static class SaveDTO {
        private Integer id;
        private String title;
        private String memo;
        private Integer calories;
        private Integer totalDistanceMeters;
        private Integer totalDurationSeconds;
        private String avgPace; // 추후 정해지면 넣자자
        private String bestPace; // 추후 정해지면 넣자자
        private List<RunSegmentResponse.DTO> segments;
        private List<PictureResponse.DTO> pictures;

        public SaveDTO(RunRecord runRecord) {
            this.id = runRecord.getId();
            this.title = runRecord.getTitle();
            this.memo = runRecord.getMemo();
            this.calories = runRecord.getCalories();
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.segments = runRecord.getRunSegments().stream()
                    .map(s -> new RunSegmentResponse.DTO(s))
                    .toList();
            this.pictures = runRecord.getPictures().stream()
                    .map(p -> new PictureResponse.DTO(p))
                    .toList();
        }

    }

    /**
     * private Integer id;
     * <p>
     * private String title;
     * <p>
     * private String memo;
     * <p>
     * private Integer calories;
     * <p>
     * private List<RunSegmentResponse.DTO> segments;
     * <p>
     * private List<PictureResponse.DTO> pictures;
     */
    @Data
    public static class DTO {
        private Integer id;
        private String title;
        private Integer calories;
        private Integer totalDistanceMeters;
        private Integer totalDurationSeconds;
        private String avgPace; // 추후 정해지면 넣자자
        private String bestPace; // 추후 정해지면 넣자자
        private List<RunSegmentResponse.DTO> segments;
        private List<PictureResponse.DTO> pictures;

        public DTO(RunRecord runRecord) {
            this.id = runRecord.getId();
            this.title = runRecord.getTitle();
            this.calories = runRecord.getCalories();
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.segments = runRecord.getRunSegments().stream()
                    .map(s -> new RunSegmentResponse.DTO(s))
                    .toList();
            this.pictures = runRecord.getPictures().stream()
                    .map(p -> new PictureResponse.DTO(p))
                    .toList();
        }

    }

}
