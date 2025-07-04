package com.example.tracky.community.challenge.domain;

import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자가 챌린지를 완료하고 보상을 획득한 이력을 기록하는 엔티티입니다.
 */
@Entity
@Getter
@Table(
        name = "user_challenge_reward_tb",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_uk_user_challenge_reward_user_challenge",
                columnNames = {"user_id", "challenge_id"}
        ) // 유저는 하나의 보상만 받는다
)
public class UserChallengeReward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user; // 보상 받은 유저

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Challenge challenge; // 보상 대상 챌린지

    /**
     * '사설 챌린지'의 경우, 획득한 리워드의 마스터 ID를 참조합니다.
     * '공개 챌린지'의 경우, 이 값은 NULL이 됩니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private RewardMaster rewardMaster;

    /**
     * 보상 지급 시점의 리워드 정보를 스냅샷으로 저장합니다. (추적 용이성)
     */
    @Column(nullable = false)
    private String rewardName; // 보상 이름
    private String rewardImageUrl; // 보상 이미지

    @CreationTimestamp
    private LocalDateTime receivedAt; // 보상 받는 날짜

    @Builder
    public UserChallengeReward(Integer id, User user, Challenge challenge, RewardMaster rewardMaster, String rewardName, String rewardImageUrl) {
        this.id = id;
        this.user = user;
        this.challenge = challenge;
        this.rewardMaster = rewardMaster;
        this.rewardName = rewardName;
        this.rewardImageUrl = rewardImageUrl;
    }

    protected UserChallengeReward() {
    }

}
