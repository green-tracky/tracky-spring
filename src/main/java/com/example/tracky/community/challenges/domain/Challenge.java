package com.example.tracky.community.challenges.domain;

import com.example.tracky._core.enums.ChallengeTypeEnum;
import com.example.tracky._core.enums.PeriodTypeEnum;
import com.example.tracky.community.challenges.dto.ChallengeRequest;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <pre>
 * 모든 챌린지의 공통 속성을 정의하는 추상(abstract) 부모 엔티티입니다..
 * JPA 상속 매핑을 통해 '공식 챌린지'와 '사설 챌린지'로 확장됩니다.
 * </pre>
 */
@Getter
@Table(
        name = "challenge_tb",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_challenge_name_period", // 제약 조건 이름
                        // 아래 컬럼들의 조합이 유일해야 함
                        columnNames = {"type", "name", "challengeYear", "challengeMonth", "weekOfMonth"}
                )
        })
@Entity
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name; // 챌린지 이름 (예: "6월 주간 챌린지")
    private String sub; // 챌린지 짧은 설명 (예: "이번 주 5km를 달려보세요.")
    private String description; // 챌린지 설명 (예: "주간 챌린지를 통해 나의 한계를...")
    @Column(nullable = false)
    private LocalDateTime startDate; // 챌린지 시작 날짜
    @Column(nullable = false)
    private LocalDateTime endDate; // 챌린지 종료 날짜
    @Column(nullable = false)
    private Integer targetDistance; // 목표 달리기 거리 (m)
    @ColumnDefault("true")
    private Boolean isInProgress; // 진행 상태. true -> 진행중, false -> 종료
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChallengeTypeEnum type; // PUBLIC, PRIVATE
    private Integer imgIndex; // 챌린지 이미지 인덱스
    private Integer challengeYear; // 년도
    private Integer challengeMonth; // 월
    private Integer weekOfMonth; // 주차 (1주차, 2주차)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PeriodTypeEnum periodType; // 주간 or 월간

    @CreationTimestamp
    private LocalDateTime createdAt; // 챌린지 생성 시간

    @UpdateTimestamp
    private LocalDateTime updatedAt; // 챌린지 수정 시간

    @ManyToOne(fetch = FetchType.LAZY)
    private User creator; // 생성자

    @Builder
    public Challenge(Integer id, String name, String sub, String description, LocalDateTime startDate, LocalDateTime endDate, Integer targetDistance, Boolean isInProgress, ChallengeTypeEnum type, Integer imgIndex, Integer challengeYear, Integer challengeMonth, Integer weekOfMonth, PeriodTypeEnum periodType, User creator) {
        this.id = id;
        this.name = name;
        this.sub = sub;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetDistance = targetDistance;
        this.isInProgress = isInProgress;
        this.type = type;
        this.imgIndex = imgIndex;
        this.challengeYear = challengeYear;
        this.challengeMonth = challengeMonth;
        this.weekOfMonth = weekOfMonth;
        this.periodType = periodType;
        this.creator = creator;
    }

    protected Challenge() {
    }

    public void closeChallenge() {
        this.isInProgress = false;
    }

    public void updateName(ChallengeRequest.UpdateDTO reqDTO) {
        this.name = Objects.requireNonNullElse(reqDTO.getName(), this.name);
    }
}