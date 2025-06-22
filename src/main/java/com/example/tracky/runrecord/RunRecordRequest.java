package com.example.tracky.runrecord;

import java.util.List;

import com.example.tracky.runrecord.picture.PictureRequest;
import com.example.tracky.runrecord.runsegment.RunSegment;
import com.example.tracky.runrecord.runsegment.RunSegmentRequest;

import lombok.Data;

public class RunRecordRequest {

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
     * private List<RunSegmentRequest.DTO> segments;
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
        private List<RunSegmentRequest.DTO> segments;
        private List<PictureRequest.DTO> pictures;

        public RunRecord toEntity(Integer userId) {
            RunRecord runRecord = RunRecord.builder()
                    .title(title)
                    .memo(memo)
                    .totalDistanceMeters(totalDistanceMeters)
                    .totalDurationSeconds(totalDurationSeconds)
                    .totalcalories(totalCalories)
                    .avgPace(avgPace)
                    .bestPace(bestPace)
                    .build();

            // 러닝 구간 변환
            List<RunSegment> runSegments = segments.stream()
                    .map(s -> s.toEntity(runRecord))
                    .toList();
            runRecord.getRunSegments().addAll(runSegments);

            // 사진은 변환 x

            return runRecord;
        }
    }
}
