package com.example.tracky.runrecord;

import com.example.tracky.runrecord.enums.RunPlaceEnum;
import com.example.tracky.runrecord.picture.Picture;
import com.example.tracky.runrecord.picture.PictureRequest;
import com.example.tracky.runrecord.runsegment.RunSegment;
import com.example.tracky.runrecord.runsegment.RunSegmentRequest;
import com.example.tracky.runrecord.utils.RunRecordUtil;
import com.example.tracky.user.User;
import lombok.Data;

import java.util.List;

public class RunRecordRequest {

    @Data
    public static class SaveDTO {
        private String title;
        private Integer calories;
        private List<RunSegmentRequest.DTO> segments;
        private List<PictureRequest.DTO> pictures;

        public RunRecord toEntity(User user) {
            RunRecord runRecord = RunRecord.builder()
                    .title(title)
                    .calories(calories)
                    .user(user)
                    .totalDistanceMeters(RunRecordUtil.calculateTotalDistanceMeters(segments))
                    .totalDurationSeconds(RunRecordUtil.calculateTotalDurationSeconds(segments))
                    .avgPace(RunRecordUtil.calculateAvgPace(segments))
                    .bestPace(RunRecordUtil.calculateBestPace(segments))
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

    @Data
    public static class UpdateDTO {
        private String title;
        private String memo;
        private Integer intensity; // 러닝 강도
        private RunPlaceEnum place; // 러닝 장소
        private List<PictureRequest.DTO> pictures; // 수정된 이미지 목록
    }
}
