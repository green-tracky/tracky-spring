package com.example.tracky.runrecord;

import java.util.List;

import com.example.tracky.runrecord.picture.PictureRequest;
import com.example.tracky.runrecord.runsegment.RunSegmentRequest;

import lombok.Data;

public class RunRecordResponse {

    @Data
    public static class DTO {
        private String title;
        private String memo;
        private Integer totalDistanceMeters;
        private Integer totalDurationSeconds;
        private Integer totalCalories;
        private Double avgPace;
        private Double bestPace;
        private List<RunSegmentRequest.DTO> segments;
        private List<PictureRequest.DTO> pictures;

        public DTO(RunRecord runRecord) {
            this.title = runRecord.getTitle();
            this.memo = runRecord.getMemo();
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.totalCalories = runRecord.getTotalcalories();
            this.avgPace = runRecord.getAvg_pace();
            this.bestPace = runRecord.getBest_pace();
            this.segments = segments;
            this.pictures = pictures;
        }

    }

}
