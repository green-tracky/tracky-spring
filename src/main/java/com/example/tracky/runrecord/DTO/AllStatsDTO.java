package com.example.tracky.runrecord.DTO;

import lombok.Data;

@Data
public class AllStatsDTO {
    private double runCount;
    private Integer avgPace;
    private Integer avgDistanceMeters; // 총 거리. 미터 단위
    private Integer avgDurationSeconds; // 총 시간. 초 단위

    public AllStatsDTO(double runCount, Integer avgPace, Integer avgDistanceMeters, Integer avgDurationSeconds) {
        this.runCount = runCount;
        this.avgPace = avgPace;
        this.avgDistanceMeters = avgDistanceMeters;
        this.avgDurationSeconds = avgDurationSeconds;
    }
}