package com.example.tracky.community.challenges.domain;

import com.example.tracky.community.challenges.enums.RewardTypeEnum;
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

    /**
     * [로직 식별자] 프로그램의 분기 처리를 위한 고유 식별자.
     * RewardTypeConverter에 의해 DB의 'reward_type' 컬럼에 "금메달", "은메달" 등으로 저장됩니다.
     */
    private RewardTypeEnum rewardType;

    private String rewardName; // 보상 이름. (금메달, 은메달, 동메달, 참가상)
    private String rewardImageUrl; // 보상 이미지

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public RewardMaster(RewardTypeEnum rewardType, String rewardName, String rewardImageUrl) {
        this.rewardType = rewardType;
        this.rewardName = rewardName;
        this.rewardImageUrl = rewardImageUrl;
    }

    protected RewardMaster() {
    }

}
