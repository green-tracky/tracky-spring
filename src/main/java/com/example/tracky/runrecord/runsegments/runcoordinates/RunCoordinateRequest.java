package com.example.tracky.runrecord.runsegments.runcoordinates;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

public class RunCoordinateRequest {

    @Data
    public static class DTO {
        @NotNull(message = "위도는 필수 입력 항목입니다.")
        @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90.0 이하이어야 합니다.")
        private Double lat;

        @NotNull(message = "경도는 필수 입력 항목입니다.")
        @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180.0 이하이어야 합니다.")
        private Double lon;

        @NotNull(message = "기록 시간은 필수 입력 항목입니다.")
        @PastOrPresent(message = "기록 시간은 현재 또는 과거의 시간이어야 합니다.")
        private LocalDateTime recordedAt;
    }

}
