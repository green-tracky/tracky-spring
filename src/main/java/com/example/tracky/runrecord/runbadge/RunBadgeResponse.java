package com.example.tracky.runrecord.runbadge;

import com.example.tracky._core.utils.DateTimeUtils;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runbadge.Enum.RunBadgeType;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchv;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RunBadgeResponse {
    @Data
    public static class ListDTO {
        private List<DTO> recents; // 최근 획득 목록
        private List<DTO> bests; // 최고기록 목록
        private List<DTO> monthly; // 월간 기록 목록

        public ListDTO(List<RunBadge> runBadges, List<RunBadgeAchv> runBadgeAchvs) {
            // 최근 획득 뱃지 5개
            this.recents = runBadgeAchvs.stream()
                    .sorted((achv1, achv2) -> achv2.getAchievedAt().compareTo(achv1.getAchievedAt()))
                    .limit(5)
                    .map(achv -> new DTO(achv))
                    .toList();

            // 획득한 뱃지 목록을 Map으로 변환합니다
            Map<Integer, RunBadgeAchv> achievedMap = runBadgeAchvs.stream()
                    .collect(Collectors.toMap(
                            achv -> achv.getRunBadge().getId(), // key
                            achv -> achv // value
                    ));

            this.bests = buildCategorizedList(runBadges, achievedMap, RunBadgeType.RECORD);
            this.monthly = buildCategorizedList(runBadges, achievedMap, RunBadgeType.MONTHLY_ACHIEVEMENT);
        }

        private List<DTO> buildCategorizedList(List<RunBadge> runBadges,
                                               Map<Integer, RunBadgeAchv> achievedMap,
                                               RunBadgeType type) {
            return runBadges.stream()
                    .filter(badge -> badge.getType() == type)
                    .map(badge -> {
                        RunBadgeAchv achieved = achievedMap.get(badge.getId());
                        if (achieved != null) {
                            return new DTO(achieved);
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
        private RunBadgeType type; // 뱃지 타입
        private LocalDateTime achievedAt; // 뱃지 획득날짜 (획득 못했으면 null)
        private Integer runRecordDistance; // 러닝 기록의 거리 (획득 못했으면 null)
        private Integer runRecordSeconds; // 러닝 기록의 시간 (획득 못했으면 null)
        private Integer runRecordPace; // 러닝 기록의 페이스 (획득 못했으면 null)
        private Boolean achieved; // 획득 유무

        // 획득한 뱃지용
        public DTO(RunBadgeAchv runBadgeAchv) {
            RunBadge runBadge = runBadgeAchv.getRunBadge();
            RunRecord runRecord = runBadgeAchv.getRunRecord();

            this.id = runBadge.getId();
            this.name = runBadge.getName();
            this.description = runBadge.getDescription();
            this.imageUrl = runBadge.getImageUrl();
            this.type = runBadge.getType();
            this.achievedAt = runBadgeAchv.getAchievedAt();
            this.runRecordDistance = runRecord.getTotalDistanceMeters();
            this.runRecordSeconds = runRecord.getTotalDurationSeconds();
            this.runRecordPace = runRecord.getAvgPace();
            this.achieved = true; // 획득했으므로 true
        }

        // 획득하지 못한 뱃지용
        public DTO(RunBadge runBadge) {
            this.id = runBadge.getId();
            this.name = runBadge.getName();
            this.description = runBadge.getDescription();
            this.imageUrl = runBadge.getImageUrl();
            this.type = runBadge.getType();
            this.achievedAt = null; // 획득 정보 없음
            this.runRecordDistance = null; // 획득 정보 없음
            this.runRecordSeconds = null; // 획득 정보 없음
            this.runRecordPace = null; // 획득 정보 없음
            this.achieved = false; // 획득 못했으므로 false
        }

    }

}
