package com.example.tracky.runrecord.runbadges;

import com.example.tracky._core.enums.RunBadgeTypeEnum;
import com.example.tracky.community.challenges.domain.RewardMaster;
import com.example.tracky.community.challenges.domain.UserChallengeReward;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runbadges.runbadgeachvs.RunBadgeAchv;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RunBadgeResponse {

    /**
     * 뱃지 보상 및 챌린지 보상이 포함되어 있습니다
     */
    @Data
    public static class GroupedBadgeListDTO {
        private List<DTO> recents; // 최근 획득 목록
        private List<DTO> bests; // 최고기록 목록
        private List<DTO> monthly; // 월간 기록 목록
        private List<DTO> challenges; // 챌린지 보상 목록

        /**
         * @param runBadges            획득 유무를 확인하기 위해서 목록 전체를 가져와야함
         * @param runBadgeAchvs        획득한 뱃지 목록
         * @param userChallengeRewards 획득한 챌린지 보상 목록
         */
        public GroupedBadgeListDTO(List<RunBadge> runBadges, List<RunBadgeAchv> runBadgeAchvs, List<UserChallengeReward> userChallengeRewards) {
            // 획득한 뱃지의 획득 횟수를 구합니다
            Map<Integer, Long> achievedBadgeCountMap = runBadgeAchvs.stream()
                    .collect(Collectors.groupingBy(
                            achv -> achv.getRunBadge().getId(),
                            Collectors.counting()
                    ));

            // 획득한 챌린지 보상의 획득 횟수를 구합니다
            Map<Integer, Long> achievedRewardCountMap = userChallengeRewards.stream()
                    .collect(Collectors.groupingBy(
                            achv -> achv.getRewardMaster().getId(),
                            Collectors.counting()
                    ));

            // 모든 뱃지 획득 기록을 뱃지 ID로 그룹핑합니다.
            Map<Integer, List<RunBadgeAchv>> allAchievedBadgesGrouped = runBadgeAchvs.stream()
                    .collect(Collectors.groupingBy(achv -> achv.getRunBadge().getId()));

            // 최근 획득 뱃지 5개
            // 1. 획득한 '러닝 뱃지'를 DTO 리스트로 변환
            List<DTO> badgeDTOs = runBadgeAchvs.stream()
                    .map(achv -> {
                        Integer badgeId = achv.getRunBadge().getId();
                        Integer count = achievedBadgeCountMap.getOrDefault(badgeId, 0L).intValue();
                        return new DTO(achv, count);
                    })
                    .toList();

            // 2. 획득한 '챌린지 보상'을 DTO 리스트로 변환
            List<DTO> challengeDTOs = userChallengeRewards.stream()
                    .map(reward -> {
                        Integer rewardId = reward.getRewardMaster().getId();
                        Integer count = achievedRewardCountMap.getOrDefault(rewardId, 0L).intValue();
                        return new DTO(reward, count);
                    })
                    .toList();

            // 3. 두 DTO 리스트를 합치고, 4. 날짜순 정렬 후, 5. 5개만 선택
            this.recents = Stream.concat(badgeDTOs.stream(), challengeDTOs.stream()) // 스트림을 하나로 합침
                    .sorted((a, b) -> b.getAchievedAt().compareTo(a.getAchievedAt())) // DTO의 achievedAt 필드 기준 내림차순 정렬
                    .limit(5) // 상위 5개만 잘라냄
                    .toList(); // 최종 결과를 리스트로 만듦

            // DTO 생성 메서드 실행
            this.bests = buildCategorizedList(runBadges, allAchievedBadgesGrouped, achievedBadgeCountMap, RunBadgeTypeEnum.RECORD);
            this.monthly = buildCategorizedList(runBadges, allAchievedBadgesGrouped, achievedBadgeCountMap, RunBadgeTypeEnum.MONTHLY_ACHIEVEMENT);
            this.challenges = buildChallengeList(userChallengeRewards, achievedRewardCountMap);
        }

        private List<DTO> buildCategorizedList(List<RunBadge> runBadges,
                                               Map<Integer, List<RunBadgeAchv>> allAchievedBadgesGrouped,
                                               Map<Integer, Long> achievedBadgeCountMap,
                                               RunBadgeTypeEnum type) {
            return runBadges.stream()
                    .filter(badge -> badge.getType() == type) // 해당 타입의 뱃지만 필터링
                    .map(badge -> {
                        // 1. 해당 뱃지의 모든 획득 기록 리스트를 가져옵니다.
                        List<RunBadgeAchv> achievementsForBadge = allAchievedBadgesGrouped.get(badge.getId());

                        // 2. 획득한 기록이 있는지 확인합니다.
                        if (achievementsForBadge != null && !achievementsForBadge.isEmpty()) {
                            // 3. 획득 기록이 있다면, 그중 가장 최신 기록을 찾습니다.
                            RunBadgeAchv mostRecentAchv = achievementsForBadge.stream()
                                    .max(Comparator.comparing(achv -> achv.getAchievedAt()))
                                    .get();

                            // 4. 전체 획득 횟수를 가져옵니다.
                            Integer count = achievedBadgeCountMap.getOrDefault(badge.getId(), 0L).intValue();

                            // 5. 가장 최신 기록과 횟수로 DTO를 만듭니다.
                            return new DTO(mostRecentAchv, count);
                        } else {
                            // 6. 획득한 기록이 없으면, 회색으로 표시될 미획득 DTO를 만듭니다.
                            return new DTO(badge);
                        }
                    })
                    .toList();
        }

        private List<DTO> buildChallengeList(List<UserChallengeReward> userChallengeRewards,
                                             Map<Integer, Long> achievedRewardCountMap) {

            // 1. 보상 ID를 기준으로 UserChallengeReward 객체들을 그룹으로 묶습니다.
            Map<Integer, List<UserChallengeReward>> rewardsGroupedById = userChallengeRewards.stream()
                    .collect(Collectors.groupingBy(reward -> reward.getRewardMaster().getId()));

            // 2. 각 그룹을 순회하며 대표 DTO를 하나씩 만듭니다.
            return rewardsGroupedById.values().stream()
                    .map(rewardsForOneType -> {
                        // 3. 그룹 내에서 가장 최근에 획득한 기록을 찾습니다.
                        UserChallengeReward mostRecentReward = rewardsForOneType.stream()
                                .max(Comparator.comparing(reward -> reward.getReceivedAt()))
                                .get();

                        // 4. 해당 보상의 전체 획득 횟수를 가져옵니다.
                        Integer count = achievedRewardCountMap.getOrDefault(mostRecentReward.getRewardMaster().getId(), 0L).intValue();

                        // 5. 가장 최신 기록과 전체 횟수를 사용해 DTO를 만듭니다.
                        return new DTO(mostRecentReward, count);
                    })
                    .toList();
        }
    }


    @Data
    public static class DTO {
        private Integer id; // badgeId or rewardMasterId
        private String name; // 뱃지 이름
        private String description; // 뱃지 설명
        private String imageUrl; // 뱃지 이미지
        private String type; // 뱃지 타입 !! 문자열로 처리
        private LocalDateTime achievedAt; // 뱃지 획득날짜 (획득 못했으면 null)
        private Integer runRecordDistance; // 러닝 기록의 거리 (획득 못했으면 null)
        private Integer runRecordSeconds; // 러닝 기록의 시간 (획득 못했으면 null)
        private Integer runRecordPace; // 러닝 기록의 페이스 (획득 못했으면 null)
        private Boolean isAchieved; // 획득 유무
        private Integer achievedCount; // 획득 횟수

        // 획득한 뱃지용 + 횟수(월간)
        public DTO(RunBadgeAchv runBadgeAchv, Integer achievedCount) {
            RunBadge runBadge = runBadgeAchv.getRunBadge();
            RunRecord runRecord = runBadgeAchv.getRunRecord();

            this.id = runBadge.getId();
            this.name = runBadge.getName();
            this.description = runBadge.getDescription();
            this.imageUrl = runBadge.getImageUrl();
            this.type = runBadge.getType().getDisplayName();
            this.achievedAt = runBadgeAchv.getAchievedAt();
            this.runRecordDistance = runRecord.getTotalDistanceMeters();
            this.runRecordSeconds = runRecord.getTotalDurationSeconds();
            this.runRecordPace = runRecord.getAvgPace();
            this.isAchieved = true; // 획득했으므로 true
            this.achievedCount = achievedCount;
        }

        // 획득한 뱃지용(최고기록)
        public DTO(RunBadgeAchv runBadgeAchv) {
            RunBadge runBadge = runBadgeAchv.getRunBadge();
            RunRecord runRecord = runBadgeAchv.getRunRecord();

            this.id = runBadge.getId();
            this.name = runBadge.getName();
            this.description = runBadge.getDescription();
            this.imageUrl = runBadge.getImageUrl();
            this.type = runBadge.getType().getDisplayName();
            this.achievedAt = runBadgeAchv.getAchievedAt();
            this.runRecordDistance = runRecord.getTotalDistanceMeters();
            this.runRecordSeconds = runRecord.getTotalDurationSeconds();
            this.runRecordPace = runRecord.getAvgPace();
            this.isAchieved = true; // 획득했으므로 true
        }

        // 획득하지 못한 뱃지용(회색)
        public DTO(RunBadge runBadge) {
            this.id = runBadge.getId();
            this.name = runBadge.getName();
            this.description = runBadge.getDescription();
            this.imageUrl = runBadge.getImageUrl();
            this.type = runBadge.getType().getDisplayName();
            this.achievedAt = null; // 획득 정보 없음
            this.runRecordDistance = null; // 획득 정보 없음
            this.runRecordSeconds = null; // 획득 정보 없음
            this.runRecordPace = null; // 획득 정보 없음
            this.isAchieved = false; // 획득 못했으므로 false
        }

        // 획득한 챌린지 보상 목록
        public DTO(UserChallengeReward userChallengeReward, Integer achievedCount) {
            RewardMaster rewardMaster = userChallengeReward.getRewardMaster();

            this.id = rewardMaster.getId();
            this.name = rewardMaster.getRewardName();
            this.description = rewardMaster.getDescription();
            this.imageUrl = rewardMaster.getRewardImageUrl();
            this.type = rewardMaster.getRewardType().getDisplayName();
            this.achievedAt = userChallengeReward.getReceivedAt();
            this.runRecordDistance = null; // 획득 정보 없음
            this.runRecordSeconds = null; // 획득 정보 없음
            this.runRecordPace = null; // 획득 정보 없음
            this.isAchieved = true; // 획득 못했으므로 false
            this.achievedCount = achievedCount; // 획득 횟수
        }

    }

    @Data
    public static class simpleDTO {
        private Integer id;
        private String name;
        private String imageUrl;

        public simpleDTO(RunBadgeAchv runBadgeAchv) {
            RunBadge runBadge = runBadgeAchv.getRunBadge();
            this.id = runBadge.getId();
            this.name = runBadge.getName();
            this.imageUrl = runBadge.getImageUrl();
        }
    }

}
