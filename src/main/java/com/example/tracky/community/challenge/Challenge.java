package com.example.tracky.community.challenge;

import com.example.tracky.community.challenge.Enum.ChallengeStatus;
import com.example.tracky.community.challenge.Enum.ChallengeStatusConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Table(name = "challenge_tb")
@Entity
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name; // 챌린지 이름
    private String sub; // 챌린지 짧은 설명
    private String description; // 챌린지 설명
    private String startDate; // 챌린지 시작 날짜
    private String endDate; // 챌린지 종료 날짜
    private Double targetDistance; // 목표 달리기 거리 (km)

    @Convert(converter = ChallengeStatusConverter.class)
    private ChallengeStatus challengeStatus; // 진행중 / 만료

    @CreationTimestamp
    private Timestamp createdAt; // 챌린지 생성 시각

    @Builder
    public Challenge(Integer id, String name, String sub, String description, String startDate, String endDate, Double targetDistance, ChallengeStatus challengeStatus) {
        this.id = id;
        this.name = name;
        this.sub = sub;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetDistance = targetDistance;
        this.challengeStatus = challengeStatus;
    }
}