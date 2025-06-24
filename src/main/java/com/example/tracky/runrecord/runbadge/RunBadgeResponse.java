package com.example.tracky.runrecord.runbadge;

import lombok.Data;

public class RunBadgeResponse {

    @Data
    public static class DTO {
        private String name; // 뱃지 이름
        private String imageUrl; // 뱃지 이미지

        public DTO(RunBadge runBadge) {
            this.name = runBadge.getName();
            this.imageUrl = runBadge.getImageUrl();
        }
    }

}
