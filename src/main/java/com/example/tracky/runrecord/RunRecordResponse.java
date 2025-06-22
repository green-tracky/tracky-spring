package com.example.tracky.runrecord;

import java.sql.Timestamp;
import java.util.List;

import com.example.tracky.runrecord.runbadge.RunBadge;

public class RunRecordResponse {

    public static class MainPageDTO {
        private Double totalDistanceMeters; // 총 거리. 미터 단위
        private Double avg_pace; // 평균 페이스
        private Integer totalDurationSeconds; // 총 시간. 초 단위
        private List<RunRecord> runRecords; // 러닝 리스트
        private List<RunBadge> runBadges; // 뱃지 리스트

        public MainPageDTO(RunRecord runRecord, List<RunRecord> runRecords, List<RunBadge> runBadges) {
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.avg_pace = runRecord.getAvg_pace();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.runRecords = runRecords;
            this.runBadges = runBadges;
        }

        class runRecords {
            private Double totalDistanceMeters; // 총 거리. 미터 단위
            private Double avg_pace; // 평균 페이스
            private Integer totalDurationSeconds; // 총 시간. 초 단위
            private List<RunBadge> runBadges; // 뱃지 리스트

            class runBadges {
                private String name; // 뱃지 이름
                private String imageUrl; // 뱃지 이미지

            }
        }
    }
}
