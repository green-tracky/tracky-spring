package com.example.tracky.runrecord.dto;

import com.example.tracky.community.challenges.domain.UserChallengeReward;
import com.example.tracky.runrecord.runbadges.runbadgeachvs.RunBadgeAchv;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AchievementHistoryItemDTO {
    private String type;
    private String name;
    private String description;
    private String imageUrl;
    private LocalDateTime achievedAt;
    private Integer achievedCount;

    private Integer runRecordDistance; // 러닝 기록의 거리 (획득 못했으면 null)
    private Integer runRecordSeconds; // 러닝 기록의 시간 (획득 못했으면 null)
    private Integer runRecordPace; // 러닝 기록의 페이스 (획득 못했으면 null)
    private Boolean isAchieved; // 획득 유무

    // 뱃지 기반 생성자
    public AchievementHistoryItemDTO(RunBadgeAchv runBadgeAchv, Integer achievedCount) {
        this.type = runBadgeAchv.getRunBadge().getType().getDisplayName();
        this.name = runBadgeAchv.getRunBadge().getName();
        this.description = runBadgeAchv.getRunBadge().getDescription();
        this.imageUrl = runBadgeAchv.getRunBadge().getImageUrl();
        this.achievedAt = runBadgeAchv.getAchievedAt();
        this.achievedCount = achievedCount;

        this.runRecordDistance = runBadgeAchv.getRunRecord().getTotalDistanceMeters();
        this.runRecordSeconds = runBadgeAchv.getRunRecord().getTotalDurationSeconds();
        this.runRecordPace = runBadgeAchv.getRunRecord().getAvgPace();
        this.isAchieved = true;
    }

    // 메달 기반 생성자
    public AchievementHistoryItemDTO(UserChallengeReward userChallengeReward, Integer achievedCount) {
        this.type = userChallengeReward.getRewardMaster().getRewardType().getDisplayName();
        this.name = userChallengeReward.getRewardMaster().getRewardName();
        this.description = userChallengeReward.getRewardMaster().getDescription();
        this.imageUrl = userChallengeReward.getRewardMaster().getRewardImageUrl();
        this.achievedAt = userChallengeReward.getReceivedAt();
        this.achievedCount = achievedCount;

        this.runRecordDistance = null;
        this.runRecordSeconds = null;
        this.runRecordPace = null;
        this.isAchieved = true;
    }
}
