package com.example.tracky.runrecord.picture;

import java.sql.Timestamp;

import com.example.tracky.runrecord.RunRecord;

import lombok.Data;

public class PictureRequest {

    /**
     * private String imgBase64;
     * <p>
     * private Double lat;
     * <p>
     * private Double lon;
     * <p>
     * private Timestamp createdAt;
     */
    @Data
    public static class DTO {
        private String imgBase64;
        private Double lat;
        private Double lon;
        private Timestamp createdAt;

        public Picture toEntity(RunRecord runRecord) {
            return Picture.builder()
                    .fileUrl(imgBase64)
                    .runRecord(runRecord)
                    .build();
        }
    }
}
