package com.example.tracky.community.challenge.publicchallenge;

import com.example.tracky.community.challenge.Challenge;
import com.example.tracky.user.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * '공식 챌린지'를 나타내는 자식 엔티티입니다.
 * 단일 리워드 정보를 고유 필드로 가집니다.
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "public_challenge_tb")
@DiscriminatorValue("OFFICIAL") // 부모 테이블의 challenge_type(DTYPE) 컬럼에 'OFFICIAL'로 저장됨
public class PublicChallenge extends Challenge {

    private String rewardTitle;
    private String rewardImageUrl;

    @Builder
    public PublicChallenge(Integer id, String name, String sub, String description, LocalDateTime startDate, LocalDateTime endDate, Integer targetDistance, Boolean isInProgress, User creator, String rewardTitle, String rewardImageUrl) {
        // 부모 클래스의 생성자를 호출하여 공통 필드를 초기화합니다.
        super(id, name, sub, description, startDate, endDate, targetDistance, isInProgress, creator);
        // 자식 클래스 고유의 필드를 초기화합니다.
        this.rewardTitle = rewardTitle;
        this.rewardImageUrl = rewardImageUrl;
    }
}
