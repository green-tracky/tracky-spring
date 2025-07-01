package com.example.tracky.runrecord.dto1;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecentRunsDTO {
    private Integer id;
    private String title;
    private Integer totalDistanceMeters;
    private Integer totalDurationSeconds;
    private Integer avgPace;
    private LocalDateTime createdAt;
    private List<RunBadgeResponse.DTO> badges; // TODO : 나중에 획득한 뱃지 들어넣기

    public RecentRunsDTO(RunRecord runRecord) {
        this.id = runRecord.getId();
        this.title = runRecord.getTitle();
        this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
        this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
        this.avgPace = runRecord.getAvgPace();
        this.createdAt = runRecord.getCreatedAt();
        this.badges = runRecord.getRunBadgeAchvs().stream()
                .map(runBadgeAchv -> new RunBadgeResponse.DTO(runBadgeAchv))
                .toList();
    }
}
