package com.example.tracky.runrecord.picture;

import lombok.Data;

import java.time.LocalDateTime;

public class PictureResponse {

    @Data
    public static class DTO {
        private String imgUrl;
        private Double lat;
        private Double lon;
        private LocalDateTime createdAt;

        public DTO(Picture picture) {
            this.imgUrl = ""; // 차후 url 넣어야 함
            this.lat = picture.getLat();
            this.lon = picture.getLon();
            this.createdAt = picture.getCreatedAt();
        }

    }

}
