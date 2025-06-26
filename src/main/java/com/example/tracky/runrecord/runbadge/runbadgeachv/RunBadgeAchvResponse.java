package com.example.tracky.runrecord.runbadge.runbadgeachv;

import com.example.tracky._core.utils.DateTimeUtils;
import com.example.tracky.runrecord.runbadge.RunBadge;
import lombok.Data;

public class RunBadgeAchvResponse {

    @Data
    public static class DTO {
        private Integer id; // badgeId
        private String name; // 뱃지 이름
        private String description; // 뱃지 설명
        private String imageUrl; // 뱃지 이미지
        private String achievedAt; // 뱃지 획득날짜

        public DTO(RunBadgeAchv runBadgeAchv) {
            RunBadge badge = runBadgeAchv.getRunBadge();
            this.id = badge.getId();
            this.name = badge.getName();
            this.description = badge.getDescription();
            this.imageUrl = badge.getImageUrl();
            this.achievedAt = DateTimeUtils.toDateTimeString(runBadgeAchv.getAchievedAt());
        }

    }

}
