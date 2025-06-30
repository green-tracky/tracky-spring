package com.example.tracky.community.challenge.privatechallenge;

import com.example.tracky.community.challenge.Challenge;
import com.example.tracky.community.challenge.rewardmaster.RewardMaster;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * '사설 챌린지'를 나타내는 자식 엔티티입니다.
 * 여러 개의 리워드 종류(RewardMaster)와 관계를 맺습니다.
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "private_challenge_tb")
@DiscriminatorValue("PRIVATE") // 부모 테이블의 challenge_type(DTYPE) 컬럼에 'PRIVATE'로 저장됨
public class PrivateChallenge extends Challenge {

    /**
     * [핵심] 이 사설 챌린지에서 제공하는 "가능한 보상 목록"입니다.
     *
     * @ManyToMany: PrivateChallenge와 RewardMaster 사이의 다대다 관계를 나타냅니다.
     * @JoinTable: 관계를 맺어줄 중간 테이블(private_challenge_reward_tb)을 지정합니다.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "private_challenge_reward_tb",
            joinColumns = @JoinColumn(name = "private_challenge_id"),
            inverseJoinColumns = @JoinColumn(name = "reward_master_id")
    )
    private List<RewardMaster> availableRewards = new ArrayList<>();

    @Builder
    public PrivateChallenge(Integer id, String name, String sub, String description, LocalDateTime startDate, LocalDateTime endDate, Integer targetDistance, Boolean isInProgress, User creator) {
        // 부모 클래스의 생성자를 호출하여 공통 필드를 초기화합니다.
        super(id, name, sub, description, startDate, endDate, targetDistance, isInProgress, creator);
    }
}
