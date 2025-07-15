package com.example.tracky.runrecord.runbadges;

import com.example.tracky._core.enums.RunBadgeTypeEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Table(name = "run_badge_tb")
@Entity
public class RunBadge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name; // 뱃지 이름
    private String description; // 뱃지 조건 설명
    private String imageUrl; // 뱃지 이미지
    @Enumerated(EnumType.STRING)
    private RunBadgeTypeEnum type; // 최고기록 or 월간업적

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public RunBadge(Integer id, String name, String description, String imageUrl, RunBadgeTypeEnum type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    // 기본생성자 사용금지
    protected RunBadge() {
    }
}
