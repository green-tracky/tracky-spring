package com.example.tracky.community.challenge.userchallengereward;

import com.example.tracky.community.challenge.Challenge;
import com.example.tracky.community.challenge.rewardmaster.RewardMaster;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자가 챌린지를 완료하고 보상을 획득한 이력을 기록하는 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user_challenge_reward_tb",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "challenge_id"})
)
public class UserChallengeReward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Challenge challenge;

    /**
     * '사설 챌린지'의 경우, 획득한 리워드의 마스터 ID를 참조합니다.
     * '공식 챌린지'의 경우, 이 값은 NULL이 됩니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private RewardMaster rewardMaster;

    /**
     * 보상 지급 시점의 리워드 정보를 스냅샷으로 저장합니다. (추적 용이성)
     */
    @Column(nullable = false)
    private String rewardName;
    private String rewardImageUrl;

    @CreationTimestamp
    private LocalDateTime receivedAt;

    @Builder
    public UserChallengeReward(Integer id, User user, Challenge challenge, RewardMaster rewardMaster, String rewardName, String rewardImageUrl) {
        this.id = id;
        this.user = user;
        this.challenge = challenge;
        this.rewardMaster = rewardMaster;
        this.rewardName = rewardName;
        this.rewardImageUrl = rewardImageUrl;
    }
}
