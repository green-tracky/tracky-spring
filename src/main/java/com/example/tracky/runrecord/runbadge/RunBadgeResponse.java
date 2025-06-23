package com.example.tracky.runrecord.runbadge;

import lombok.AllArgsConstructor;
import lombok.Data;

public class RunBadgeResponse {
    
    @Data
    public static class DTO {
        private Integer id;
        private String name;
        private String description;
        private String imageUrl;

        public DTO(RunBadge runBadge) {
            this.id = runBadge.getId();
            this.name = runBadge.getName();
            this.description = runBadge.getDescription();
            this.imageUrl = runBadge.getImageUrl();
        }
        
    }

}
