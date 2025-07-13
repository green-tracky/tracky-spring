package com.example.tracky.runrecord.pictures;

import com.example.tracky.runrecord.RunRecord;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public class PictureRequest {

    @Data
    public static class DTO {
        @NotBlank(message = "파일 URL은 필수 입력 항목입니다.")
        @URL(message = "올바른 URL 형식이 아닙니다.")
        private String fileUrl;

        @NotNull(message = "위도는 필수 입력 항목입니다.")
        @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90.0 이하이어야 합니다.")
        private Double lat;

        @NotNull(message = "경도는 필수 입력 항목입니다.")
        @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180.0 이하이어야 합니다.")
        private Double lon;

        @NotNull(message = "저장 시간은 필수 입력 항목입니다.")
        @PastOrPresent(message = "저장 시간은 현재 또는 과거의 시간이어야 합니다.")
        private LocalDateTime savedAt;

        public Picture toEntity(RunRecord runRecord) {
            return Picture.builder()
                    .fileUrl(fileUrl)
                    .lat(lat)
                    .lon(lon)
                    .savedAt(savedAt)
                    .runRecord(runRecord)
                    .build();
        }
    }
}
