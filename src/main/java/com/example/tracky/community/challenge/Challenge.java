package com.example.tracky.community.challenge;

import com.example.tracky.community.challenge.Enum.ChallengeTypeEnum;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Table(name = "challenge_tb")
@Entity
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name; // 챌린지 이름 (예: "6월 주간 챌린지")
    private String sub; // 챌린지 짧은 설명 (예: "이번 주 5km를 달려보세요.")
    private String description; // 챌린지 설명 (예: "주간 챌린지를 통해 나의 한계를...")
    private LocalDateTime startDate; // 챌린지 시작 날짜
    private LocalDateTime endDate; // 챌린지 종료 날짜
    private Integer targetDistance; // 목표 달리기 거리 (m)
    private Boolean isInProgress; // 진행 상태. true -> 진행중, false -> 종료

    @CreationTimestamp
    private LocalDateTime createdAt; // 챌린지 생성 시간

    @UpdateTimestamp
    private LocalDateTime updatedAt; // 챌린지 수정 시간

    private ChallengeTypeEnum challengeType; // (공개 | 사설)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User creator; // 생성자

    @Builder
    public Challenge(Integer id, String name, String sub, String description, LocalDateTime startDate, LocalDateTime endDate, Integer targetDistance, Boolean isInProgress, ChallengeTypeEnum challengeType, User creator) {
        this.id = id;
        this.name = name;
        this.sub = sub;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetDistance = targetDistance;
        this.isInProgress = isInProgress;
        this.challengeType = challengeType;
        this.creator = creator;
    }
}