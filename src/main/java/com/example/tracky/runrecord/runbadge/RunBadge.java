package com.example.tracky.runrecord.runbadge;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
