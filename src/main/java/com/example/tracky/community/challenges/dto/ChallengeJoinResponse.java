package com.example.tracky.community.challenges.dto;

import com.example.tracky.community.challenges.domain.ChallengeJoin;
import lombok.Data;

import java.time.LocalDateTime;

public class ChallengeJoinResponse {

    @Data
    public static class DTO {
        private Integer id;
        private Integer challengeId;
        private Integer userId;
        private LocalDateTime joinDate;

        public DTO(ChallengeJoin challengeJoin) {
            this.id = challengeJoin.getId();
            this.challengeId = challengeJoin.getChallenge().getId();
            this.userId = challengeJoin.getUser().getId();
            this.joinDate = challengeJoin.getJoinDate();
        }
    }
}
