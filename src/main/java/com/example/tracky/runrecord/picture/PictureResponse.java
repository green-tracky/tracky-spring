package com.example.tracky.runrecord.picture;

import java.sql.Timestamp;

import lombok.Data;

public class PictureResponse {

    /**
     * private String imgBase64;
     * <p>
     * private Integer duration;
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
        private Integer duration;
        private Double lat;
        private Double lon;
        private Timestamp createdAt;

        public DTO(Picture picture) {
            this.imgBase64 = ""; // 차후 url 넣어야 함
            this.duration = picture.getDuration();
            this.lat = picture.getLat();
            this.lon = picture.getLon();
            this.createdAt = picture.getCreatedAt();
        }

    }

}
