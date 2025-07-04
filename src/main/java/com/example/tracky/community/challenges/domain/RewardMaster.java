package com.example.tracky.community.challenges.domain;

import com.example.tracky.community.challenges.enums.ChallengeTypeEnum;
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

    private ChallengeTypeEnum type; // 공개, 사설

    @Column(unique = true)
    private String rewardName; // 보상 이름. (금메달, 은메달, 동메달, 참가상, 7월 15k 챌린지)
    private String rewardImageUrl; // 보상 이미지

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public RewardMaster(ChallengeTypeEnum type, String rewardName, String rewardImageUrl) {
        this.type = type;
        this.rewardName = rewardName;
        this.rewardImageUrl = rewardImageUrl;
    }

    protected RewardMaster() {
    }

}
