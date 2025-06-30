package com.example.tracky.runrecord.runbadge;

import com.example.tracky.runrecord.runbadge.enums.RunBadgeType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
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
    private RunBadgeType type; // 뱃지 타입 필드 추가. RunBadgeType 타입 확인 바람

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public RunBadge(Integer id, String name, String description, String imageUrl, RunBadgeType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.type = type;
    }

}
