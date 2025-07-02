package com.example.tracky.runrecord.dto;

import com.example.tracky.runrecord.RunRecord;
import lombok.Data;

@Data
public class AvgStatsDTO {
    private Integer totalDistanceMeters; // 총 거리. 미터 단위 [StatsDTO]
    private Integer totalDurationSeconds; // 총 시간. 초 단위
    private Integer recodeCount;
    private Integer avgPace;

    public AvgStatsDTO(RunRecord runRecord, Integer recodeCount, Integer avgPace) {
        this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
        this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
        this.recodeCount = recodeCount;
        this.avgPace = avgPace;
    }
}
