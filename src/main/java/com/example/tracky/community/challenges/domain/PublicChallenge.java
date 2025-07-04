package com.example.tracky.community.challenges.domain;

import com.example.tracky.community.challenges.enums.ChallengeTypeEnum;
import com.example.tracky.user.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * '공식 챌린지'를 나타내는 자식 엔티티입니다.
 * 단일 리워드 정보를 고유 필드로 가집니다.
 */
@Entity
@Getter
@Table(name = "public_challenge_tb")
@DiscriminatorValue(ChallengeTypeEnum.PUBLIC_TYPE) // 부모 테이블의 challenge_type(DTYPE) 컬럼에 'PUBLIC' 로 저장됨
public class PublicChallenge extends Challenge {

    private String rewardName; // 공개 챌린지 보상 이름
    private String rewardImageUrl; // 공개 챌린지 보상 이미지

    @Builder
    public PublicChallenge(Integer id, String name, String sub, String description, LocalDateTime startDate, LocalDateTime endDate, Integer targetDistance, Boolean isInProgress, User creator, String rewardName, String rewardImageUrl) {
        // 부모 클래스의 생성자를 호출하여 공통 필드를 초기화합니다.
        super(id, name, sub, description, startDate, endDate, targetDistance, isInProgress, creator);
        // 자식 클래스 고유의 필드를 초기화합니다.
        this.rewardName = rewardName;
        this.rewardImageUrl = rewardImageUrl;
    }

    protected PublicChallenge() {
    }

}
