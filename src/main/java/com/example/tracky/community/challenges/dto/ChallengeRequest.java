package com.example.tracky.community.challenges.dto;

import com.example.tracky._core.enums.ChallengeTypeEnum;
import com.example.tracky._core.enums.PeriodTypeEnum;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.user.User;
import lombok.Data;

import java.time.LocalDateTime;

public class ChallengeRequest {

    @Data
    public static class SaveDTO {
        private String name;
        private String imageUrl;
        private Integer targetDistance; // 거리 m 단위
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        public Challenge toEntity(User user) {
            return Challenge.builder()
                    .name(name)
                    .imageUrl(imageUrl)
                    .startDate(startDate)
                    .endDate(endDate)
                    .targetDistance(targetDistance)
                    .isInProgress(true)
                    .type(ChallengeTypeEnum.PRIVATE)
                    .periodType(PeriodTypeEnum.ETC)
                    .creator(user)
                    .build();
        }
    }
}
