package com.example.tracky.community.challenge.dto;

import com.example.tracky.community.challenge.domain.Challenge;
import com.example.tracky.community.challenge.domain.PrivateChallenge;
import com.example.tracky.community.challenge.domain.PublicChallenge;
import com.example.tracky.community.challenge.enums.ChallengeTypeEnum;
import com.example.tracky.community.challenge.utils.ChallengeUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
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
                       Map<Integer, Integer> totalDistancesMap,
                       Map<Integer, Integer> participantCountsMap) {

            LocalDateTime now = LocalDateTime.now();

            // 1. 내가 참여한 챌린지를 '진행 중'과 '지난 챌린지'로 분류하여 생성
            this.myChallenges = joinedChallenges.stream()
                    .filter(c -> c.getEndDate().isAfter(now))
                    .map(c -> new ChallengeItemDTO(c, totalDistancesMap.getOrDefault(c.getId(), 0)))
                    .toList();

            this.pastChallenges = joinedChallenges.stream()
                    .filter(c -> c.getEndDate().isBefore(now))
                    .map(c -> new ChallengeItemDTO(c, totalDistancesMap.getOrDefault(c.getId(), 0), true))
                    .toList();

            // 2. 참여 가능한 공개 챌린지 목록 생성
            this.joinableChallenges = unjoinedChallenges.stream()
                    .map(publicChallenge -> new ChallengeItemDTO(publicChallenge))
                    .toList();

            // 3. 추천 챌린지 선정 (참여 가능한 챌린지 중 랜덤으로 하나)
            if (!unjoinedChallenges.isEmpty()) {
                PublicChallenge recommended = unjoinedChallenges.get(new Random().nextInt(unjoinedChallenges.size()));
                Integer count = participantCountsMap.getOrDefault(recommended.getId(), 0);
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
                this.title = ChallengeUtil.formatTitle(challenge.getTargetDistance());
                this.imageUrl = challenge.getRewardImageUrl();
                this.participantCount = participantCount;
            }
        }

        // 공통 DTO
        @Data
        class ChallengeItemDTO {
            private Integer id;
            private String title; // 이미지 없을때 식별용
            private String rewardName; // 챌린지 이름
            private String sub; // 챌린지 짧은 설명
            private Integer remainingTime; // 챌린지 종료까지 남은 시간. 초단위
            private Integer myDistance; // 챌린지 기간의 나의 누적 거리. m 단위
            private Integer targetDistance; // 챌린지 목표거리. m 단위
            private Boolean isInProgress; // 챌린지 진행 상태
            private LocalDateTime endDate; // 챌린지 종료 날짜

            // '나의 챌린지' (진행 중) 용 생성자
            ChallengeItemDTO(Challenge challenge, Integer achievedDistance) {
                this.id = challenge.getId();
                this.title = ChallengeUtil.formatTitle(challenge.getTargetDistance());
                this.rewardName = challenge.getName();
                this.sub = null;
                this.remainingTime = ChallengeUtil.calculateRemainingSeconds(challenge.getEndDate());
                this.myDistance = achievedDistance;
                this.targetDistance = challenge.getTargetDistance();
                this.isInProgress = challenge.getIsInProgress();
                this.endDate = null;
            }

            // '참여하기' (미참여) 용 생성자
            ChallengeItemDTO(PublicChallenge challenge) {
                this.id = challenge.getId();
                this.title = ChallengeUtil.formatTitle(challenge.getTargetDistance());
                this.rewardName = challenge.getName();
                this.sub = challenge.getSub();
                this.remainingTime = ChallengeUtil.calculateRemainingSeconds(challenge.getEndDate());
                this.myDistance = null; // 아직 참여 안 했으므로 null
                this.targetDistance = null;
                this.isInProgress = challenge.getIsInProgress();
                this.endDate = null;
            }

            // '이전 챌린지' (종료) 용 생성자
            ChallengeItemDTO(Challenge challenge, Integer achievedDistance, boolean isPast) {
                this.id = challenge.getId();
                this.title = ChallengeUtil.formatTitle(challenge.getTargetDistance());
                this.rewardName = challenge.getName();
                this.sub = null;
                this.remainingTime = 0; // 종료됐으므로 0
                this.myDistance = achievedDistance;
                this.targetDistance = challenge.getTargetDistance();
                this.isInProgress = challenge.getIsInProgress(); // 종료됐으므로 false
                this.endDate = challenge.getEndDate();
            }
        }
    }

    @Data
    public static class DetailDTO {
        // --- 공통 정보 ---
        private Integer id;
        private String title; // 이미지 없을때 식별용
        private String name; // 챌린지 이름
        private String sub; // 챌린지 짧은 설명
        private String description; // 챌린지 설명
        private LocalDateTime startDate; // 챌린지 시작 날짜
        private LocalDateTime endDate; // 챌린지 종료 날짜
        private Integer targetDistance; // 챌린지 목표거리. m 단위
        private Integer remainingTime; // 챌린지 종료까지 남은 시간. 초단위
        private Boolean isInProgress; // 챌린지 진행 상태
        private Integer participantCount; // 챌린지 참가자 수
        private String creatorName; // 생성자 이름. 공식이면 null 넣기
        private ChallengeTypeEnum challengeType; // "PUBLIC" 또는 "PRIVATE"
        private Boolean isJoined; // 사용자의 참여 여부
        private Integer rank; // 순위 정보
        private Integer myDistance; // 챌린지 기간의 나의 누적 거리. m 단위

        // --- 리워드 정보 ---
        private List<RewardItemDTO> rewards;

        // --- 생성자들 ---

        /**
         * 1. 미참여 공식 챌린지 조회 시 사용하는 생성자
         */
        public DetailDTO(PublicChallenge challenge, Integer participantCount) {
            this.id = challenge.getId();
            this.title = challenge.getTargetDistance() / 1000 + "K";
            this.name = challenge.getName();
            this.sub = challenge.getSub();
            this.description = challenge.getDescription();
            this.remainingTime = ChallengeUtil.calculateRemainingSeconds(challenge.getEndDate());
            this.targetDistance = challenge.getTargetDistance();
            this.isInProgress = challenge.getIsInProgress();
            this.startDate = challenge.getStartDate();
            this.endDate = challenge.getEndDate();
            this.participantCount = participantCount;
            this.creatorName = null; // 공식 챌린지이므로 null
            this.challengeType = ChallengeTypeEnum.PUBLIC;
            this.isJoined = false; // 미참여 상태
            this.rank = null; // 참여 안 했으므로 순위 없음
            this.myDistance = null; // 참여 안 했으므로 달성 거리 없음

            // 공식 챌린지의 단일 리워드 정보 생성
            this.rewards = Collections.singletonList(new RewardItemDTO(challenge.getRewardName(), challenge.getRewardImageUrl(), null));
        }

        /**
         * 2. 참여 중인 공식 챌린지 조회 시 사용하는 생성자 (진행/종료 모두 포함)
         */
        public DetailDTO(PublicChallenge challenge, Integer participantCount, Double myDistance, Integer myRank) {
            // 미참여 생성자를 먼저 호출하여 공통 정보 채우기
            this(challenge, participantCount);

            // 사용자 특화 정보 덮어쓰기
            this.isJoined = true; // 참여 상태
            this.rank = myRank;
            this.myDistance = myDistance.intValue();

            // 리워드 상태 업데이트 (예시: 달성 여부에 따라 '달성' 표시)
            boolean isAchieved = myDistance >= challenge.getTargetDistance();
            this.rewards.get(0).setStatus(isAchieved ? "달성" : "미달성");
        }

        /**
         * 3. 참여 중인 사설 챌린지 조회 시 사용하는 생성자 (진행/종료 모두 포함)
         */
        public DetailDTO(PrivateChallenge challenge, Integer participantCount, Double myDistance, Integer myRank) {
            // 공통 정보 설정
            this.id = challenge.getId();
            this.title = challenge.getTargetDistance() / 1000 + "K";
            this.name = challenge.getName();
            this.sub = challenge.getSub();
            this.description = challenge.getDescription();
            this.remainingTime = ChallengeUtil.calculateRemainingSeconds(challenge.getEndDate());
            this.targetDistance = challenge.getTargetDistance();
            this.isInProgress = challenge.getIsInProgress();
            this.startDate = challenge.getStartDate();
            this.endDate = challenge.getEndDate();
            this.participantCount = participantCount;
            this.creatorName = challenge.getCreator().getUsername(); // 사설 챌린지 크리에이터 이름 설정
            this.challengeType = ChallengeTypeEnum.PRIVATE;

            // 사용자 특화 정보 설정
            this.isJoined = true; // 참여 상태
            this.rank = myRank;
            this.myDistance = myDistance.intValue();

            // 사설 챌린지의 여러 리워드 목록을 생성
            this.rewards = challenge.getAvailableRewards().stream()
                    .map(rewardLink -> {
                        // 각 리워드별 달성 여부 로직이 필요하다면 여기에 추가
                        return new RewardItemDTO(
                                rewardLink.getRewardMaster().getRewardName(),
                                rewardLink.getRewardMaster().getRewardImageUrl(),
                                "달성" // 예시로 '달성' 상태 고정
                        );
                    })
                    .toList();
        }

        @Data
        class RewardItemDTO {
            private String rewardName; // 리워드 이름
            private String rewardImageUrl; // 리워드 이미지
            private String status; // "달성", "미달성", null 등

            public RewardItemDTO(String rewardName, String rewardImageUrl, String status) {
                this.rewardName = rewardName;
                this.rewardImageUrl = rewardImageUrl;
                this.status = status;
            }
        }

    }

}
