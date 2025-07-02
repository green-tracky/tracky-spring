package com.example.tracky.community.challenge.domain;

import com.example.tracky.community.challenge.enums.ChallengeTypeEnum;
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
@DiscriminatorValue(ChallengeTypeEnum.PRIVATE_TYPE)
public class PrivateChallenge extends Challenge {

    /**
     * <pre>
     * cascade = CascadeType.ALL -> 챌린지를 저장/삭제할 때, 보상 목록도 알아서 같이 처리됨 (생명주기 일치)
     * orphanRemoval = true -> 이 목록에서 보상을 제거하면, DB에서도 해당 보상 데이터가 실제로 삭제됨
     * </pre>
     */
    @OneToMany(mappedBy = "privateChallenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrivateChallengeReward> availableRewards = new ArrayList<>(); // 사설챌린지의 보상목록

    @Builder
    public PrivateChallenge(Integer id, String name, String sub, String description, LocalDateTime startDate, LocalDateTime endDate, Integer targetDistance, Boolean isInProgress, User creator) {
        super(id, name, sub, description, startDate, endDate, targetDistance, isInProgress, creator);
    }

}
