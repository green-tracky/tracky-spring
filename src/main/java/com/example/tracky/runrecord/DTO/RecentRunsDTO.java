package com.example.tracky.runrecord.DTO;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import lombok.Data;

import java.util.List;

@Data
public class RecentRunsDTO {
    private String title;
    private Integer totalDistanceMeters;
    private Integer totalDurationSeconds;
    private Integer avgPace;
    private String createdAt;
    private List<RunBadgeResponse.DTO> badges; // TODO : 나중에 획득한 뱃지 들어넣기

    public RecentRunsDTO(RunRecord runRecord, List<RunBadgeResponse.DTO> badges, Integer avgPace) {
        this.title = runRecord.getTitle();
        this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
        this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
        this.avgPace = avgPace;
        this.createdAt = runRecord.getCreatedAt().toString();
        this.badges = badges;
    }
}
