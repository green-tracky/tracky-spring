package com.example.tracky.community.challenges.domain;

import com.example.tracky._core.enums.ChallengeTypeEnum;
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
@Table(name = "user_challenge_reward_tb")
public class UserChallengeReward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChallengeTypeEnum type; // 공개, 사설 구분용

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user; // 보상 받은 유저

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Challenge challenge; // 보상 대상 챌린지

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private RewardMaster rewardMaster; // 보상

    @CreationTimestamp
    private LocalDateTime receivedAt; // 보상 받는 날짜

    @Builder
    public UserChallengeReward(Integer id, ChallengeTypeEnum type, User user, Challenge challenge, RewardMaster rewardMaster) {
        this.id = id;
        this.type = type;
        this.user = user;
        this.challenge = challenge;
        this.rewardMaster = rewardMaster;
    }

    protected UserChallengeReward() {
    }

}
