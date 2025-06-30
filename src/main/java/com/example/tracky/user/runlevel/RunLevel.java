package com.example.tracky.user.runlevel;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Table(name = "run_level_tb")
@Entity
public class RunLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name; // 레벨 이름
    private Integer minDistance; // 해당 레벨의 조건 범위 시작 (m)
    private Integer maxDistance; // 해당 레벨의 조건 범위 끝 (m)
    private String description; // 레벨 설명 (예: "0~49.99킬로미터" 등)
    private String imageUrl; // 레벨에 대응하는 이미지 URL
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
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }
}


