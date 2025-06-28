package com.example.tracky.runrecord.runbadge.runbadgeachv;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runbadge.Enum.RunBadgeType;
import com.example.tracky.runrecord.runbadge.RunBadge;
import lombok.Data;

import java.time.LocalDateTime;

public class RunBadgeAchvResponse {
    @Data
    public static class DTO {
        private Integer id; // badgeId
        private String name; // 뱃지 이름
        private String description; // 뱃지 설명
        private String imageUrl; // 뱃지 이미지
        private RunBadgeType type; // 뱃지 타입
        private LocalDateTime achievedAt; // 뱃지 획득날짜
        private Integer runRecordDistance; // 러닝 기록의 거리
        private Integer runRecordSeconds; // 러닝 기록의 시간
        private Integer runRecordPace; // 러닝 기록의 페이스


        public DTO(RunBadgeAchv runBadgeAchv) {
            RunBadge runBadge = runBadgeAchv.getRunBadge();
            RunRecord runRecord = runBadgeAchv.getRunRecord();

            this.id = runBadge.getId();
            this.name = runBadge.getName();
            this.description = runBadge.getDescription();
            this.imageUrl = runBadge.getImageUrl();
            this.type = runBadge.getType();
            this.achievedAt = runBadgeAchv.getAchievedAt();
            this.runRecordDistance = runRecord.getTotalDistanceMeters();
            this.runRecordSeconds = runRecord.getTotalDurationSeconds();
            this.runRecordPace = runRecord.getAvgPace();

        }

    }

}
