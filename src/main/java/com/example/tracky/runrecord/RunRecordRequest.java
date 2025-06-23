package com.example.tracky.runrecord;

import java.util.List;

import com.example.tracky.runrecord.picture.Picture;
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
     * private Integer calories;
     * <p>
     * private List<RunSegmentRequest.DTO> segments;
     * <p>
     * private List<PictureRequest.DTO> pictures;
     */
    @Data
    public static class DTO {
        private String title;
        private String memo;
        private Integer calories;
        private List<RunSegmentRequest.DTO> segments;
        private List<PictureRequest.DTO> pictures;

        public RunRecord toEntity(Integer userId) {
            RunRecord runRecord = RunRecord.builder()
                    .title(title)
                    .memo(memo)
                    // .totalDistanceMeters(totalDistanceMeters) 구간별 거리 합계 로직 추가
                    // .totalDurationSeconds(totalDurationSeconds) 구간별 시간 합계 로직 추가
                    .calories(calories)
                    .build();

            // 러닝 구간 변환
            List<RunSegment> runSegments = segments.stream()
                    .map(s -> s.toEntity(runRecord))
                    .toList();
            runRecord.getRunSegments().addAll(runSegments);

            // 사진 변환
            // 없으면 변환 x
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
