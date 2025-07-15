package com.example.tracky.runrecord.dto;

import com.example.tracky.user.runlevel.RunLevel;
import lombok.Data;

@Data
public class RunLevelDTO {
    private String name;
    private Integer totalDistance; // 사용자 누적 거리
    private Integer distanceToNextLevel; // 다음 레벨까지 필요한 거리

    public RunLevelDTO(RunLevel runLevel, Integer totalDistance, Integer distanceToNextLevel) {
        this.name = runLevel.getName();
        this.totalDistance = totalDistance;
        this.distanceToNextLevel = distanceToNextLevel;
    }
}
