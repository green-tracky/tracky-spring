package com.example.tracky.runrecord.pictures;

import com.example.tracky.runrecord.RunRecord;
import lombok.Data;

import java.time.LocalDateTime;

public class PictureRequest {

    @Data
    public static class DTO {
        private String imgBase64;
        private Double lat;
        private Double lon;
        private LocalDateTime createdAt;

        public Picture toEntity(RunRecord runRecord) {
            return Picture.builder()
                    .fileUrl("") // 차후 나중에 url 로 변환 해야함
                    .lat(lat)
                    .lon(lon)
                    .createdAt(createdAt)
                    .runRecord(runRecord)
                    .build();
        }
    }
}
