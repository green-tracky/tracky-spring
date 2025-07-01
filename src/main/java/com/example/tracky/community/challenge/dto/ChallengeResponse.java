package com.example.tracky.community.challenge.dto;

import com.example.tracky.community.challenge.domain.Challenge;
import com.example.tracky.community.challenge.domain.PublicChallenge;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChallengeResponse {

    // 최종 응답을 감싸는 메인 DTO
    @Data
    public static class MainDTO {
        private RecommendedDTO recommendedChallenge; // 추천 챌린지. 공개 챌린지 중 랜덤으로 하나
        private List<ChallengeItemDTO> myChallenges; // 내가 참가하고 있는 챌린지 목록
        private List<ChallengeItemDTO> joinableChallenges; // 참가할 수 있는 공개 챌린지 목록
        private List<ChallengeItemDTO> pastChallenges; // 내가 참여했던 챌린지 목록

        public MainDTO(List<Challenge> joinedChallenges, // ChallengeJoin 에서 해당되는 Challenge를 가져온 것
                       List<PublicChallenge> unjoinedChallenges,
                       Map<Integer, Integer> achievedDistances,
                       Map<Integer, Integer> participantCounts) {

            LocalDateTime now = LocalDateTime.now();

            // 1. 내가 참여한 챌린지를 '진행 중'과 '지난 챌린지'로 분류하여 생성
            this.myChallenges = joinedChallenges.stream()
                    .filter(c -> c.getEndDate().isAfter(now))
                    .map(c -> new ChallengeItemDTO(c, achievedDistances.getOrDefault(c.getId(), 0)))
                    .toList();

            this.pastChallenges = joinedChallenges.stream()
                    .filter(c -> c.getEndDate().isBefore(now))
                    .map(c -> new ChallengeItemDTO(c, achievedDistances.getOrDefault(c.getId(), 0), true))
                    .toList();

            // 2. 참여 가능한 공개 챌린지 목록 생성
            this.joinableChallenges = unjoinedChallenges.stream()
                    .map(ChallengeItemDTO::new)
                    .toList();

            // 3. 추천 챌린지 선정 (참여 가능한 챌린지 중 랜덤으로 하나)
            if (!unjoinedChallenges.isEmpty()) {
                PublicChallenge recommended = unjoinedChallenges.get(new Random().nextInt(unjoinedChallenges.size()));
                Integer count = participantCounts.getOrDefault(recommended.getId(), 0);
                this.recommendedChallenge = new RecommendedDTO(recommended, count);
            }
        }

        // 추천 챌린지 DTO
        @Data
        class RecommendedDTO {
            private Integer id;
            private String title; // 이미지 없을 때 식별용
            private String imageUrl; // 이미지
            private Integer participantCount; // 참가자 수

            // PublicChallenge 엔티티와 참가자 수를 받아 필드를 초기화합니다.
            public RecommendedDTO(PublicChallenge challenge, Integer participantCount) {
                this.id = challenge.getId();
                this.title = formatTitle(challenge.getTargetDistance());
                this.imageUrl = challenge.getRewardImageUrl();
                this.participantCount = participantCount;
            }
        }

        // 공통 DTO
        @Data
        class ChallengeItemDTO {
            private Integer id;
            private String title; // 이미지 없을때 식별용
            private String name; // 챌린지 이름
            private String sub; // 챌린지 짧은 설명
            private Integer remainingTime; // 챌린지 종료까지 남은 시간. 초단위
            private Integer myDistance; // 챌린지 기간의 나의 누적 거리. m 단위
            private Integer targetDistance; // 챌린지 목표거리. m 단위
            private Boolean isInProgress; // 챌린지 진행 상태
            private LocalDateTime endDate; // 챌린지 종료 날짜

            // '나의 챌린지' (진행 중) 용 생성자
            ChallengeItemDTO(Challenge challenge, Integer achievedDistance) {
                this.id = challenge.getId();
                this.title = formatTitle(challenge.getTargetDistance());
                this.name = challenge.getName();
                this.sub = null;
                this.remainingTime = calculateRemainingSeconds(challenge.getEndDate());
                this.myDistance = achievedDistance;
                this.targetDistance = challenge.getTargetDistance();
                this.isInProgress = challenge.getIsInProgress();
                this.endDate = null;
            }

            // '참여하기' (미참여) 용 생성자
            ChallengeItemDTO(PublicChallenge challenge) {
                this.id = challenge.getId();
                this.title = formatTitle(challenge.getTargetDistance());
                this.name = challenge.getName();
                this.sub = challenge.getSub();
                this.remainingTime = calculateRemainingSeconds(challenge.getEndDate());
                this.myDistance = null; // 아직 참여 안 했으므로 null
                this.targetDistance = null;
                this.isInProgress = challenge.getIsInProgress();
                this.endDate = null;
            }

            // '이전 챌린지' (종료) 용 생성자
            ChallengeItemDTO(Challenge challenge, Integer achievedDistance, boolean isPast) {
                this.id = challenge.getId();
                this.title = formatTitle(challenge.getTargetDistance());
                this.name = challenge.getName();
                this.sub = null;
                this.remainingTime = 0; // 종료됐으므로 0
                this.myDistance = achievedDistance;
                this.targetDistance = challenge.getTargetDistance();
                this.isInProgress = challenge.getIsInProgress(); // 종료됐으므로 false
                this.endDate = challenge.getEndDate();
            }
        }
    }

    /**
     * 챌린지 이미지 대체용 문자열
     *
     * @param targetDistance
     * @return
     */
    private static String formatTitle(Integer targetDistance) {
        return targetDistance / 1000 + "K";
    }

    /**
     * 남은 시간 초단위로 알려줌
     *
     * @param endDate
     * @return
     */
    private static Integer calculateRemainingSeconds(LocalDateTime endDate) {
        Duration duration = Duration.between(LocalDateTime.now(), endDate);
        return duration.isNegative() ? 0 : (int) duration.getSeconds();
    }

}
