package com.example.tracky.runrecord.dto;

import com.example.tracky.community.challenge.domain.UserChallengeReward;
import com.example.tracky.runrecord.runbadge.enums.RunBadgeTypeEnum;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchv;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AchievementHistoryItemDTO {
    private String type; // "badge" or "medal"
    private String name;
    private String imageUrl;
    private LocalDateTime achievedAt;
    private Integer count;

    private RunBadgeTypeEnum badgeType; // 뱃지 타입
    private Integer runRecordDistance; // 러닝 기록의 거리 (획득 못했으면 null)
    private Integer runRecordSeconds; // 러닝 기록의 시간 (획득 못했으면 null)
    private Integer runRecordPace; // 러닝 기록의 페이스 (획득 못했으면 null)
    private Boolean isAchieved; // 획득 유무

    // 뱃지 기반 생성자
    public AchievementHistoryItemDTO(RunBadgeAchv runBadgeAchv, Integer count) {
        this.type = "뱃지";
        this.name = runBadgeAchv.getRunBadge().getName();
        this.imageUrl = runBadgeAchv.getRunBadge().getImageUrl();
        this.achievedAt = runBadgeAchv.getAchievedAt();
        this.count = count;

        this.badgeType = runBadgeAchv.getRunBadge().getType();
        this.runRecordDistance = runBadgeAchv.getRunRecord().getTotalDistanceMeters();
        this.runRecordSeconds = runBadgeAchv.getRunRecord().getTotalDurationSeconds();
        this.runRecordPace = runBadgeAchv.getRunRecord().getAvgPace();
        this.isAchieved = true;
    }

    // 메달 기반 생성자
    public AchievementHistoryItemDTO(UserChallengeReward userChallengeReward, Integer count) {
        this.type = "메달";
        this.name = userChallengeReward.getRewardMaster().getRewardName();
        this.imageUrl = userChallengeReward.getRewardMaster().getRewardImageUrl();
        this.achievedAt = userChallengeReward.getReceivedAt();
        this.count = count;

        this.badgeType = null;
        this.runRecordDistance = null;
        this.runRecordSeconds = null;
        this.runRecordPace = null;
        this.isAchieved = null;
    }
}
