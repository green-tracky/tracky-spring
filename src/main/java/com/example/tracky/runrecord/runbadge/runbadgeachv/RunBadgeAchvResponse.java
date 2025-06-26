package com.example.tracky.runrecord.runbadge.runbadgeachv;

import com.example.tracky._core.utils.DateTimeUtils;
import com.example.tracky.runrecord.runbadge.RunBadge;
import lombok.Data;

public class RunBadgeAchvResponse {

    @Data
    public static class DTO {
        private Integer runBadgeId;
        private String name;
        private String description;
        private String imageUrl;
        private String achievedAt;

        public DTO(RunBadgeAchv runBadgeAchv) {
            RunBadge badge = runBadgeAchv.getRunBadge();
            this.runBadgeId = badge.getId();
            this.name = badge.getName();
            this.description = badge.getDescription();
            this.imageUrl = badge.getImageUrl();
            this.achievedAt = DateTimeUtils.toDateTimeString(runBadgeAchv.getAchievedAt());
        }

    }

}
