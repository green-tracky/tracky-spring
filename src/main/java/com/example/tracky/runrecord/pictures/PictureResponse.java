package com.example.tracky.runrecord.pictures;

import lombok.Data;

import java.time.LocalDateTime;

public class PictureResponse {

    @Data
    public static class DTO {
        private String fileUrl;
        private Double lat;
        private Double lon;
        private LocalDateTime savedAt;

        public DTO(Picture picture) {
            this.fileUrl = picture.getFileUrl();
            this.lat = picture.getLat();
            this.lon = picture.getLon();
            this.savedAt = picture.getSavedAt();
        }

    }

}
