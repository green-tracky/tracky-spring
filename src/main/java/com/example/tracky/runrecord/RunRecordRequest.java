package com.example.tracky.runrecord;

import com.example.tracky._core.constants.Constants;
import com.example.tracky._core.enums.RunPlaceTypeEnum;
import com.example.tracky.runrecord.pictures.Picture;
import com.example.tracky.runrecord.pictures.PictureRequest;
import com.example.tracky.runrecord.runsegments.RunSegment;
import com.example.tracky.runrecord.runsegments.RunSegmentRequest;
import com.example.tracky.runrecord.utils.RunRecordUtil;
import com.example.tracky.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

public class RunRecordRequest {

    @Data
    public static class SaveDTO {
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        @Size(max = Constants.TITLE_LENGTH, message = "제목은 " + Constants.TITLE_LENGTH + "자를 초과할 수 없습니다.")
        private String title;
        @NotNull(message = "칼로리는 필수 입력 항목입니다.")
        @PositiveOrZero(message = "칼로리는 0 이상이어야 합니다.")
        private Integer calories;
        @NotEmpty(message = "운동 경로 데이터는 최소 하나 이상 포함되어야 합니다.")
        @Valid // 리스트 안의 객체들을 검증하기 위해 필수!
        private List<RunSegmentRequest.DTO> segments;
        @Valid
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

        @Size(max = Constants.TITLE_LENGTH, message = "제목은 " + Constants.TITLE_LENGTH + "자를 초과할 수 없습니다.")
        private String title;

        @Size(max = Constants.MEMO_LENGTH, message = "메모는 " + Constants.MEMO_LENGTH + "자를 초과할 수 없습니다.")
        private String memo;

        @Min(value = 1, message = "운동 강도는 1 이상이어야 합니다.")
        @Max(value = 10, message = "운동 강도는 10 이하이어야 합니다.")
        private Integer intensity; // 러닝 강도 1~10

        private RunPlaceTypeEnum place; // 러닝 장소 도로|산길|트랙|null
    }
}
