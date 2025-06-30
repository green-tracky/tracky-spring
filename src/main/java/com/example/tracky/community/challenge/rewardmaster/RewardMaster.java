package com.example.tracky.community.challenge.rewardmaster;

import com.example.tracky.community.challenge.enums.RewardTypeEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 모든 사설 챌린지에서 공통으로 사용할 리워드의 종류를 정의하는 마스터 엔티티입니다.
 * 이 테이블의 데이터는 거의 변경되지 않으며, 데이터 중복을 방지하는 핵심 역할을 합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reward_master_tb")
public class RewardMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 프로그램 코드와 약속된, 절대 변하지 않는 고유 식별자입니다.
     * 이 값을 기준으로 Java Enum과 매핑하고, 비즈니스 로직을 작성합니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RewardTypeEnum rewardType;

    /**
     * 사용자에게 보여주기 위한 기본 이름. 이 값은 언제든지 변경될 수 있습니다.
     */
    @Column(nullable = false)
    private String defaultName;

    private String defaultImageUrl;

    @Builder
    public RewardMaster(Integer id, RewardTypeEnum rewardType, String defaultName, String defaultImageUrl) {
        this.id = id;
        this.rewardType = rewardType;
        this.defaultName = defaultName;
        this.defaultImageUrl = defaultImageUrl;
    }
}
