package com.example.tracky.community.challenge.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

/**
 * [핵심] PrivateChallenge와 RewardMaster의 다대다 관계를 해결하기 위한 연결 엔티티입니다.
 * 중간 테이블(private_challenge_reward_tb)에 직접 매핑됩니다.
 */
@Entity
@Getter
@Table(name = "private_challenge_reward_tb")
public class PrivateChallengeReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PrivateChallenge privateChallenge; // 사설 챌린지

    @ManyToOne(fetch = FetchType.LAZY)
    private RewardMaster rewardMaster; // 보상 종류. 금, 은, 동, 참여

    @Builder
    public PrivateChallengeReward(Integer id, PrivateChallenge privateChallenge, RewardMaster rewardMaster) {
        this.id = id;
        this.privateChallenge = privateChallenge;
        this.rewardMaster = rewardMaster;
    }

    protected PrivateChallengeReward() {
    }

}
