package com.example.tracky.community.challenge;

import com.example.tracky.community.challenge.Enum.ChallengeStatus;
import lombok.Data;

public class ChallengeResponse {

    @Data
    public static class DTO {
        private Integer id;
        private String name; // 챌린지 이름
        private String sub; // 챌린지 짧은 설명
        private String description; // 챌린지 설명
        private String startDate; // 챌린지 시작 날짜
        private String endDate; // 챌린지 종료 날짜
        private Double targetDistance; // 목표 달리기 거리 (km)
        private ChallengeStatus challengeStatus; // 진행중 / 만료
        private String createdAt;

        public DTO(Challenge challenge) {
            this.id = challenge.getId();
            this.name = challenge.getName();
            this.sub = challenge.getSub();
            this.description = challenge.getDescription();
            this.startDate = challenge.getStartDate();
            this.endDate = challenge.getEndDate();
            this.targetDistance = challenge.getTargetDistance();
            this.challengeStatus = challenge.getChallengeStatus();
            this.createdAt = challenge.getCreatedAt();
        }
    }

}

