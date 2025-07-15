package com.example.tracky.community.challenges.domain;

import com.example.tracky._core.enums.ChallengeTypeEnum;
import com.example.tracky._core.enums.RewardTypeEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "reward_master_tb")
public class RewardMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RewardTypeEnum rewardType; // 챌린지 우승자, 챌린지 수상자
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChallengeTypeEnum challengeType; // 공개, 사설
    @Column(unique = true, nullable = false)
    private String rewardName; // 보상 이름. (금메달, 은메달, 동메달, 참가상, 7월 15k 챌린지)

    private String rewardImageUrl; // 보상 이미지
    private String description; // 챌린지를 완료하셨습니다!

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public RewardMaster(Integer id, String description, RewardTypeEnum rewardType, ChallengeTypeEnum challengeType, String rewardName, String rewardImageUrl) {
        this.id = id;
        this.description = description;
        this.rewardType = rewardType;
        this.challengeType = challengeType;
        this.rewardName = rewardName;
        this.rewardImageUrl = rewardImageUrl;
    }

    protected RewardMaster() {
    }

}
