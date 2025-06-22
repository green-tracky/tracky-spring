package com.example.tracky.runrecord;

import java.util.List;

import com.example.tracky.runrecord.picture.PictureRequest;
import com.example.tracky.runrecord.runsegment.RunSegmentResponse;

import lombok.Data;

public class RunRecordResponse {

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
        private String title;
        private String memo;
        private Integer totalDistanceMeters;
        private Integer totalDurationSeconds;
        private Integer totalCalories;
        private Double avgPace;
        private Double bestPace;
        private List<RunSegmentResponse.DTO> segments;
        private List<PictureRequest.DTO> pictures;

        public DTO(RunRecord runRecord) {
            this.title = runRecord.getTitle();
            this.memo = runRecord.getMemo();
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.totalCalories = runRecord.getTotalcalories();
            this.avgPace = runRecord.getAvgPace();
            this.bestPace = runRecord.getBestPace();
            this.segments = runRecord.getRunSegments().stream()
                    .map(s -> new RunSegmentResponse.DTO(s))
                    .toList();

        }

    }

}
