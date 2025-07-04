package com.example.tracky.community.challenges.dto;

import com.example.tracky.community.challenges.domain.UserChallengeReward;
import lombok.Data;

import java.time.LocalDateTime;

public class ChallengeRewardResponse {
    @Data
    public static class DTO {
        private String rewardName; // 메달 이름
        private String rewardImageUrl; // 메달 이미지
        private LocalDateTime receivedAt; // 메달 받는 날짜
        private Integer CountReward; // 메달 받은 횟수

        public DTO(UserChallengeReward UserChallengeReward) {
            this.rewardName = UserChallengeReward.getRewardName();
            this.rewardImageUrl = UserChallengeReward.getRewardImageUrl();
            this.receivedAt = UserChallengeReward.getReceivedAt();
//            this.CountReward = countReward;
        }
    }
}
