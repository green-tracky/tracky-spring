package com.example.tracky.runrecord.runbadge;

import lombok.AllArgsConstructor;
import lombok.Data;

public class RunBadgeResponse {
    
    @Data
    @AllArgsConstructor
    public static class RunBadgeResponseDto {
        private Integer id;
        private String name;
        private String description;
        private String imageUrl;

    }

}
