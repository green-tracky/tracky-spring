package com.example.tracky.runrecord;

import java.sql.Timestamp;
import java.util.List;

import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.runrecord.runsegment.RunSegment;
import com.example.tracky.runrecord.runsegment.runcoordinate.RunCoordinate;
import com.example.tracky.runrecord.utils.RunRecordUtil;

import java.util.List;

import com.example.tracky.runrecord.picture.PictureResponse;
import com.example.tracky.runrecord.runsegment.RunSegmentResponse;

import lombok.Data;

public class RunRecordResponse {

    @Data
    public static class MainPageDTO {
        private Integer totalDistanceMeters; // 총 거리. 미터 단위
        private Integer totalDurationSeconds; // 총 시간. 초 단위
        private Integer countRecode;
        private String avgPace;
        private List<DTO> runRecords;
        private List<RunBadge> runBadges;

        public MainPageDTO(RunRecord runRecord, String avgPace, List<DTO> runRecords, List<RunBadge> runBadges) {
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.countRecode = runRecords.size();
            this.avgPace = avgPace;
            this.runRecords = runRecords;
            this.runBadges = runBadges;
        }
        
        }

    /**
     * private String title;
     * <p>
     * private String memo;
     * <p>
     * private Integer totalDistanceMeters;
     * <p>
     * private Integer totalDurationSeconds;
     * <p>
     * private Integer totalCalories;
     * <p>
     * private Double avgPace;
     * <p>
     * private Double bestPace;
     * <p>
     * private List<RunSegmentResponse.DTO> segments;
     * <p>
     * private List<PictureRequest.DTO> pictures;
     */
    @Data
    public static class DTO {
        private Integer id;
        private String title;
        private String memo;
        private Integer totalDistanceMeters;
        private Integer totalDurationSeconds;
        private Integer calories;
        private List<RunSegmentResponse.DTO> segments;
        private List<PictureResponse.DTO> pictures;

        public DTO(RunRecord runRecord) {
            this.id = runRecord.getId();
            this.title = runRecord.getTitle();
            this.memo = runRecord.getMemo();
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.calories = runRecord.getCalories();
            this.segments = runRecord.getRunSegments().stream()
                    .map(s -> new RunSegmentResponse.DTO(s))
                    .toList();
            this.pictures = runRecord.getPictures().stream()
                    .map(p -> new PictureResponse.DTO(p))
                    .toList();
        }

    }

}
