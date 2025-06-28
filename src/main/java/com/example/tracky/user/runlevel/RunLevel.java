package com.example.tracky.user.runlevel;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "run_level_tb")
@Entity
public class RunLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name; // 레벨 이름
    private Integer min_distance; // 해당 레벨의 조건 범위 시작 (m)
    private Integer max_distance; // 해당 레벨의 조건 범위 끝 (m)
    private String description; // 레벨 설명 (예: "초보 러너" 등)
    private String imageUrl; // 레벨에 대응하는 이미지 URL
    private Integer sortOrder; // 레벨 정렬용 값

    @Builder
    public RunLevel(Integer id, String name, Integer min_distance, Integer max_distance, String description, String imageUrl, Integer sortOrder) {
        this.id = id;
        this.name = name;
        this.min_distance = min_distance;
        this.max_distance = max_distance;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }
}


