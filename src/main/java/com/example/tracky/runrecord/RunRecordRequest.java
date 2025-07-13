package com.example.tracky.runrecord;

import com.example.tracky._core.enums.RunPlaceTypeEnum;
import com.example.tracky.runrecord.pictures.Picture;
import com.example.tracky.runrecord.pictures.PictureRequest;
import com.example.tracky.runrecord.runsegments.RunSegment;
import com.example.tracky.runrecord.runsegments.RunSegmentRequest;
import com.example.tracky.runrecord.utils.RunRecordUtil;
import com.example.tracky.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

public class RunRecordRequest {

    @Data
    public static class SaveDTO {
        @NotBlank(message = "러닝 제목을 입력해주세요.")
        @Size(max = 100, message = "제목은 최대 100자까지 입력할 수 있습니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\s.,!?\"'()\\-]*$",
                message = "제목에 허용되지 않은 특수문자가 포함되어 있습니다."
        )
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
        private RunPlaceTypeEnum place; // 러닝 장소
        private List<PictureRequest.DTO> pictures; // 수정된 이미지 목록
    }
}
