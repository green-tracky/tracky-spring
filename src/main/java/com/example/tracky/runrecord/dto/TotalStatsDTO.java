package com.example.tracky.runrecord.dto;

import lombok.Data;

@Data
public class TotalStatsDTO {
    private double runCountPerWeek;
    private Integer avgPace;
    private Integer avgDistanceMetersPerRun; // 총 거리. 미터 단위
    private Integer avgDurationSecondsPerRun; // 총 시간. 초 단위

    public TotalStatsDTO(double runCountPerWeek, Integer avgPace, Integer avgDistanceMetersPerRun, Integer avgDurationSecondsPerRun) {
        this.runCountPerWeek = runCountPerWeek;
        this.avgPace = avgPace;
        this.avgDistanceMetersPerRun = avgDistanceMetersPerRun;
        this.avgDurationSecondsPerRun = avgDurationSecondsPerRun;
    }
}