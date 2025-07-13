package com.example.tracky.community.challenges.dto;

import com.example.tracky._core.enums.ChallengeTypeEnum;
import com.example.tracky._core.enums.PeriodTypeEnum;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.domain.ChallengeInvite;
import com.example.tracky.community.challenges.domain.RewardMaster;
import com.example.tracky.community.challenges.utils.ChallengeUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ChallengeResponse {

    // 최종 응답을 감싸는 메인 DTO
    @Data
    public static class MainDTO {
        private RecommendedDTO recommendedChallenge; // 추천 챌린지. 공개 챌린지 중 랜덤으로 하나
        private List<ChallengeInviteItemDTO> inviteChallenges; // 초대온 챌린지 목록
        private List<ChallengeItemDTO> myChallenges; // 내가 참가하고 있는 챌린지 목록
        private List<ChallengeItemDTO> joinableChallenges; // 참가할 수 있는 공개 챌린지 목록
        private List<ChallengeItemDTO> pastChallenges; // 내가 참여했던 챌린지 목록

        public MainDTO(List<Challenge> joinedChallenges, // ChallengeJoin 에서 해당되는 Challenge를 가져온 것
                       List<Challenge> unjoinedChallenges,
                       Map<Integer, Integer> totalDistancesMap,
                       Map<Integer, Integer> participantCountsMap,
                       List<ChallengeInvite> inviteChallenges) {

            // 1. 내가 참여한 챌린지를 '진행 중'과 '지난 챌린지'로 분류하여 생성
            this.myChallenges = joinedChallenges.stream()
                    .filter(challenge -> challenge.getIsInProgress())
                    .map(challenge -> new ChallengeItemDTO(challenge, totalDistancesMap.getOrDefault(challenge.getId(), 0)))
                    .toList();

            this.pastChallenges = joinedChallenges.stream()
                    .filter(challenge -> !challenge.getIsInProgress())
                    .map(challenge -> new ChallengeItemDTO(challenge, totalDistancesMap.getOrDefault(challenge.getId(), 0), true))
                    .toList();

            // 2. 참여 가능한 공개 챌린지 목록 생성
            this.joinableChallenges = unjoinedChallenges.stream()
                    .map(challenge -> new ChallengeItemDTO(challenge))
                    .toList();

            // 3. 추천 챌린지 선정 (참여 가능한 챌린지 중 랜덤으로 하나)
            if (!unjoinedChallenges.isEmpty()) {
                Challenge recommended = unjoinedChallenges.get(new Random().nextInt(unjoinedChallenges.size()));
                Integer participantCount = participantCountsMap.getOrDefault(recommended.getId(), 0);
                this.recommendedChallenge = new RecommendedDTO(recommended, participantCount);
            }

            // 4. 초대된 챌린지 목록
            this.inviteChallenges = inviteChallenges.stream()
                    .map(challengeInvite -> new ChallengeInviteItemDTO(challengeInvite))
                    .toList();
        }

        // 추천 챌린지 DTO
        @Data
        class RecommendedDTO {
            private Integer id;
            private String name;
            private Integer participantCount; // 참가자 수
            private ChallengeTypeEnum type; // "공개" 또는 "사설"

            // PublicChallenge 엔티티와 참가자 수를 받아 필드를 초기화합니다.
            public RecommendedDTO(Challenge challenge, Integer participantCount) {
                this.id = challenge.getId();
                this.name = challenge.getName();
                this.participantCount = participantCount;
                this.type = challenge.getType();
            }
        }

        // 공통 DTO
        @Data
        class ChallengeItemDTO {
            private Integer id;
            private String name; // 챌린지 이름
            private String sub; // 챌린지 짧은 설명
            private Integer remainingTime; // 챌린지 종료까지 남은 시간. 초단위
            private Integer myDistance; // 챌린지 기간의 나의 누적 거리. m 단위
            private Integer targetDistance; // 챌린지 목표거리. m 단위
            private Boolean isInProgress; // 챌린지 진행 상태
            private LocalDateTime endDate; // 챌린지 종료 날짜
            private ChallengeTypeEnum type; // "공개" 또는 "사설"

            // '나의 챌린지' (진행 중) 용 생성자
            ChallengeItemDTO(Challenge challenge, Integer achievedDistance) {
                this.id = challenge.getId();
                this.name = challenge.getName();
                this.sub = null;
                this.remainingTime = ChallengeUtil.calculateRemainingSeconds(challenge.getEndDate());
                this.myDistance = achievedDistance;
                this.targetDistance = challenge.getTargetDistance();
                this.isInProgress = challenge.getIsInProgress();
                this.endDate = null;
                this.type = challenge.getType();
            }

            // '참여하기' (미참여) 용 생성자
            ChallengeItemDTO(Challenge challenge) {
                this.id = challenge.getId();
                this.name = challenge.getName();
                this.sub = challenge.getSub();
                this.remainingTime = ChallengeUtil.calculateRemainingSeconds(challenge.getEndDate());
                this.myDistance = null; // 아직 참여 안 했으므로 null
                this.targetDistance = null;
                this.isInProgress = challenge.getIsInProgress();
                this.endDate = null;
                this.type = challenge.getType();
            }

            // '이전 챌린지' (종료) 용 생성자
            ChallengeItemDTO(Challenge challenge, Integer achievedDistance, boolean isPast) {
                this.id = challenge.getId();
                this.name = challenge.getName();
                this.sub = null;
                this.remainingTime = 0; // 종료됐으므로 0
                this.myDistance = achievedDistance;
                this.targetDistance = challenge.getTargetDistance();
                this.isInProgress = challenge.getIsInProgress(); // 종료됐으므로 false
                this.endDate = challenge.getEndDate();
                this.type = challenge.getType();
            }
        }

        @Data
        class ChallengeInviteItemDTO {
            private ChallengeInviteInfoDTO challengeInfo; // 챌린지 정보
            private String fromUsername; // 초대 보낸 사람 이름
            private Integer challengeInviteId; // 챌린지 초대 db 아이디

            public ChallengeInviteItemDTO(ChallengeInvite invite) {
                this.challengeInfo = new ChallengeInviteInfoDTO(invite.getChallenge());
                this.fromUsername = invite.getFromUser().getUsername();
                this.challengeInviteId = invite.getId();
            }

            @Data
            class ChallengeInviteInfoDTO {
                private Integer id;
                private String name; // 챌린지 이름
                private String sub; // 챌린지 짧은 설명
                private Integer remainingTime; // 챌린지 종료까지 남은 시간. 초단위
                private Integer myDistance; // 챌린지 기간의 나의 누적 거리. m 단위
                private Integer targetDistance; // 챌린지 목표거리. m 단위
                private Boolean isInProgress; // 챌린지 진행 상태
                private LocalDateTime startDate; // 챌린지 종료 날짜
                private LocalDateTime endDate; // 챌린지 종료 날짜
                private ChallengeTypeEnum type; // "공개" 또는 "사설"

                public ChallengeInviteInfoDTO(Challenge challenge) {
                    this.id = challenge.getId();
                    this.name = challenge.getName();
                    this.sub = challenge.getSub();
                    this.remainingTime = ChallengeUtil.calculateRemainingSeconds(challenge.getEndDate());
                    this.myDistance = null;
                    this.targetDistance = challenge.getTargetDistance();
                    this.isInProgress = challenge.getIsInProgress();
                    this.startDate = challenge.getStartDate();
                    this.endDate = challenge.getEndDate();
                    this.type = challenge.getType();
                }
            }
        }
    }

    @Data
    public static class DetailDTO {
        // --- 공통 정보 ---
        private Integer id;
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
        private ChallengeTypeEnum type; // "공개" 또는 "사설"
        private Boolean isJoined; // 사용자의 참여 여부
        private Integer rank; // 순위 정보
        private Integer myDistance; // 챌린지 기간의 나의 누적 거리. m 단위
        private Integer imgIndex; // 이미지 인덱스

        // --- 리워드 정보 ---
        private List<RewardItemDTO> rewards;

        /**
         * 모든 챌린지(공개/사설) 공통 생성자
         */
        public DetailDTO(Challenge challenge,
                         Integer participantCount,
                         Integer myDistance,
                         Integer myRank,
                         Boolean isJoined,
                         List<RewardMaster> rewardMasters) {
            this.id = challenge.getId();
            this.name = challenge.getName();
            this.sub = challenge.getSub();
            this.description = challenge.getDescription();
            this.startDate = challenge.getStartDate();
            this.endDate = challenge.getEndDate();
            this.targetDistance = challenge.getTargetDistance();
            this.remainingTime = ChallengeUtil.calculateRemainingSeconds(challenge.getEndDate());
            this.isInProgress = challenge.getIsInProgress();
            this.participantCount = participantCount;
            this.creatorName = challenge.getType() == ChallengeTypeEnum.PRIVATE
                    ? challenge.getCreator().getUsername() : null;
            this.type = challenge.getType(); // "PUBLIC" or "PRIVATE"
            this.isJoined = isJoined;
            this.rank = myRank;
            this.myDistance = myDistance;
            this.rewards = rewardMasters.stream()
                    .map(rewardMaster -> new RewardItemDTO(rewardMaster))
                    .toList();
            this.imgIndex = challenge.getImgIndex();
        }

        @Data
        class RewardItemDTO {
            private String rewardName; // 리워드 이름
            private String rewardImageUrl; // 리워드 이미지
            private String status;

            public RewardItemDTO(RewardMaster rewardMaster) {
                this.rewardName = rewardMaster.getRewardName();
                this.rewardImageUrl = rewardMaster.getRewardImageUrl();
                this.status = "달성";
            }
        }
    }

    @Data
    public static class SaveDTO {
        private Integer id;
        private String name;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer targetDistance;
        private Integer remainingTime;
        private Boolean isInProgress;
        private Integer participantCount;
        private String creatorName;
        private ChallengeTypeEnum type;
        private Boolean isJoined;
        private Integer imgIndex;
        private PeriodTypeEnum periodType;

        public SaveDTO(Challenge challenge) {
            this.id = challenge.getId();
            this.name = challenge.getName();
            this.startDate = challenge.getStartDate();
            this.endDate = challenge.getEndDate();
            this.targetDistance = challenge.getTargetDistance();
            this.remainingTime = ChallengeUtil.calculateRemainingSeconds(challenge.getEndDate());
            this.isInProgress = challenge.getIsInProgress();
            this.participantCount = 1;
            this.creatorName = challenge.getCreator().getUsername();
            this.type = challenge.getType();
            this.isJoined = true;
            this.imgIndex = challenge.getImgIndex();
            this.periodType = challenge.getPeriodType();
        }
    }

    @Data
    public static class UpdateDTO {
        private Integer id;
        private String name;

        public UpdateDTO(Challenge challenge) {
            this.id = challenge.getId();
            this.name = challenge.getName();
        }
    }
}
