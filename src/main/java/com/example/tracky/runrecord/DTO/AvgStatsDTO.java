package com.example.tracky.runrecord.DTO;

import com.example.tracky.runrecord.RunRecord;
import lombok.Data;

@Data
public class AvgStatsDTO {
    private Integer totalDistanceMeters; // 총 거리. 미터 단위 [StatsDTO]
    private Integer totalDurationSeconds; // 총 시간. 초 단위
    private Integer countRecode;
    private Integer avgPace;

    public AvgStatsDTO(RunRecord runRecord, Integer countRecode, Integer avgPace) {
        this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
        this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
        this.countRecode = countRecode;
        this.avgPace = avgPace;
    }
}
