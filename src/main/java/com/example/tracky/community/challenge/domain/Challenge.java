package com.example.tracky.community.challenge.domain;

import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * <pre>
 * 모든 챌린지의 공통 속성을 정의하는 추상(abstract) 부모 엔티티입니다.
 * JPA 상속 매핑을 통해 '공식 챌린지'와 '사설 챌린지'로 확장됩니다.
 * </pre>
 */
@Getter
@Table(name = "challenge_tb")
@Entity
@Inheritance(strategy = InheritanceType.JOINED) // [핵심] JOINED 상속 전략 사용. 조회할 때는 내부적으로 JOIN 쿼리 발생
@DiscriminatorColumn(name = "challenge_type") // 자식 타입을 구분할 컬럼(DTYPE)의 이름을 'challenge_type'으로 지정. 부모테이블에 컬럼이 자동으로 생김
public abstract class Challenge { // 직접 인스턴스화 시켜서 사용하지 않음
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // jpa 영속성 관리 null 불가
    @JoinColumn(nullable = false) // db 제약조건
    private User creator; // 생성자

    public Challenge(Integer id, String name, String sub, String description, LocalDateTime startDate, LocalDateTime endDate, Integer targetDistance, Boolean isInProgress, User creator) {
        this.id = id;
        this.name = name;
        this.sub = sub;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetDistance = targetDistance;
        this.isInProgress = isInProgress;
        this.creator = creator;
    }

    protected Challenge() {
    }
}