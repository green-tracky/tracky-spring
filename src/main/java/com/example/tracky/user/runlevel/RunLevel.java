package com.example.tracky.user.runlevel;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Table(name = "run_level_tb")
@Entity
public class RunLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name; // 레벨 이름

    @Column(nullable = false)
    private Integer minDistance; // 해당 레벨의 조건 범위 시작 (m)
    @Column(nullable = false)
    private Integer maxDistance; // 해당 레벨의 조건 범위 끝 (m)

    private String description; // 레벨 설명 (예: "0~49.99킬로미터" 등 )

    @Column(nullable = false)
    private Integer sortOrder; // 레벨 정렬용 값 (0~)

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public RunLevel(Integer id, String name, Integer minDistance, Integer maxDistance, String description, String imageUrl, Integer sortOrder) {
        this.id = id;
        this.name = name;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    // 기본 생성자 사용금지
    protected RunLevel() {
    }
}


