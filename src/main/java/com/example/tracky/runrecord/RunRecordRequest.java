package com.example.tracky.runrecord;

import java.util.List;

import com.example.tracky.runrecord.picture.Picture;
import com.example.tracky.runrecord.picture.PictureRequest;
import com.example.tracky.runrecord.runsegment.RunSegment;
import com.example.tracky.runrecord.runsegment.RunSegmentRequest;

import lombok.Data;

public class RunRecordRequest {

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

        public RunRecord toEntity(Integer userId) {
            RunRecord runRecord = RunRecord.builder()
                    .title(title)
                    .memo(memo)
                    .totalDistanceMeters(totalDistanceMeters)
                    .totalDurationSeconds(totalDurationSeconds)
                    .totalcalories(totalCalories)
                    .avg_pace(avgPace)
                    .best_pace(bestPace)
                    .build();

            List<RunSegment> runSegments = segments.stream()
                    .map(s -> s.toEntity(runRecord))
                    .toList();
            runRecord.getRunSegments().addAll(runSegments);

            if (pictures != null) {
                List<Picture> pictureEntities = pictures.stream()
                        .map(p -> p.toEntity(runRecord))
                        .toList();
                runRecord.getPictures().addAll(pictureEntities);
            }

            return runRecord;
        }
    }
}
