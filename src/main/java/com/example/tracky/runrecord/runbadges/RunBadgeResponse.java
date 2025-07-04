package com.example.tracky.runrecord.runbadges;

import com.example.tracky.community.challenges.domain.RewardMaster;
import com.example.tracky.community.challenges.domain.UserChallengeReward;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runbadges.enums.RunBadgeTypeEnum;
import com.example.tracky.runrecord.runbadges.runbadgeachvs.RunBadgeAchv;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RunBadgeResponse {

    /**
     * 뱃지 보상 및 챌린지 보상이 포함되어 있습니다
     */
    @Data
    public static class GroupedBadgeListDTO {
        private List<DTO> recents; // 최근 획득 목록
        private List<DTO> bests; // 최고기록 목록
        private List<DTO> monthly; // 월간 기록 목록

        public GroupedBadgeListDTO(List<RunBadge> runBadges, List<RunBadgeAchv> runBadgeAchvs) {
            // 획득한 뱃지 목록을 Map으로 변환합니다
            Map<Integer, RunBadgeAchv> achievedMap = runBadgeAchvs.stream()
                    .collect(Collectors.toMap(
                            achv -> achv.getRunBadge().getId(), // key
                            achv -> achv // value
                    ));

            // 획득한 뱃지의 획득 횟수를 구합니다
            Map<Integer, Long> achievedCountMap = runBadgeAchvs.stream()
                    .collect(Collectors.groupingBy(
                            achv -> achv.getRunBadge().getId(),
                            Collectors.counting()
                    ));


            // 최근 획득 뱃지 5개
            this.recents = runBadgeAchvs.stream()
                    .sorted((achv1, achv2) -> achv2.getAchievedAt().compareTo(achv1.getAchievedAt()))
                    .limit(5)
                    .map(achv -> {
                        Integer badgeId = achv.getRunBadge().getId();
                        Integer count = achievedCountMap.getOrDefault(badgeId, 0L).intValue();
                        return new DTO(achv, count);
                    })
                    .toList();
            this.bests = buildCategorizedList(runBadges, achievedMap, achievedCountMap, RunBadgeTypeEnum.RECORD);
            this.monthly = buildCategorizedList(runBadges, achievedMap, achievedCountMap, RunBadgeTypeEnum.MONTHLY_ACHIEVEMENT);
        }

        private List<DTO> buildCategorizedList(List<RunBadge> runBadges,
                                               Map<Integer, RunBadgeAchv> achievedMap,
                                               Map<Integer, Long> achievedCountMap,
                                               RunBadgeTypeEnum type) {
            return runBadges.stream()
                    .filter(badge -> badge.getType() == type)
                    .map(badge -> {
                        RunBadgeAchv isAchieved = achievedMap.get(badge.getId());
                        Integer count = achievedCountMap.getOrDefault(badge.getId(), 0L).intValue();
                        if (isAchieved != null) {
                            return new DTO(isAchieved, count);
                        } else {
                            return new DTO(badge);
                        }
                    })
                    .toList();
        }
    }


    @Data
    public static class DTO {
        private Integer id; // badgeId
        private String name; // 뱃지 이름
        private String description; // 뱃지 설명
        private String imageUrl; // 뱃지 이미지
        private String type; // 뱃지 타입 !! 나중에 타입으로 처리하겠음. 뱃지 타입과 챌린지 보상 타입 다르기 때문
        private LocalDateTime achievedAt; // 뱃지 획득날짜 (획득 못했으면 null)
        private Integer runRecordDistance; // 러닝 기록의 거리 (획득 못했으면 null)
        private Integer runRecordSeconds; // 러닝 기록의 시간 (획득 못했으면 null)
        private Integer runRecordPace; // 러닝 기록의 페이스 (획득 못했으면 null)
        private Boolean isAchieved; // 획득 유무
        private Integer achievedCount; // 획득 횟수

        // 획득한 뱃지용 + 횟수
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

        // 획득한 뱃지용
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

        // 획득하지 못한 뱃지용
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

        // 획득하지 못한 뱃지용
        public DTO(UserChallengeReward userChallengeReward) {
            RewardMaster rewardMaster = userChallengeReward.getRewardMaster();

            this.id = rewardMaster.getId();
            this.name = rewardMaster.getRewardName();
            this.description = "챌린지를 완료하셨습니다";
            this.imageUrl = rewardMaster.getRewardImageUrl();
            this.type = rewardMaster.getType().getDisplayName();
            this.achievedAt = userChallengeReward.getReceivedAt(); // 획득 정보 없음
            this.runRecordDistance = null; // 획득 정보 없음
            this.runRecordSeconds = null; // 획득 정보 없음
            this.runRecordPace = null; // 획득 정보 없음
            this.isAchieved = true; // 획득 못했으므로 false
        }

    }

}
