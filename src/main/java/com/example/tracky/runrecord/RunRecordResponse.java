package com.example.tracky.runrecord;

import java.sql.Timestamp;
import java.util.List;

import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.runrecord.runsegment.RunSegment;
import com.example.tracky.runrecord.runsegment.runcoordinate.RunCoordinate;

import lombok.Data;

public class RunRecordResponse {

    @Data
    public static class MainPageDTO {
        private Integer totalDistanceMeters; // 모든 RunRecord 거리. 미터 단위
        private Integer countRunRecord; // RunRecord 갯수
        private Double avgPace; // 모든 RunRecord 평균 페이스
        private Integer totalDurationSeconds; // 모든 RunRecord 시간. 초 단위
        private List<RunRecord> runRecords; // 러닝 리스트
        private List<RunBadge> runBadges; // 뱃지 리스트

        public MainPageDTO(RunRecord runRecord, List<RunRecord> runRecords, List<RunBadge> runBadges) {
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.avgPace = runRecord.getAvgPace();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.countRunRecord = runRecords.size();
            this.runRecords = runRecords;
            this.runBadges = runBadges;
        }

         public static class RunRecords {
            private List<RunSegment> runSegments; // 총 구간

            public static class RunSegments {
                private Integer distanceMeters; // 구간 이동거리. 미터 단위
                private Integer durationSeconds; // 구간 소요시간. 초 단위
                private Timestamp startDate; // 구간 시작 시간. 프론트에서 받아야 한다
                private Timestamp endDate; // 구간 종료 시간. 프론트에서 받아야 한다
                private Double pace; // 구간 페이스. 초 단위 예) 253.85초
                private Integer calories; // 구간 소비 칼로리.
                private List<RunCoordinate> runCoordinates;

                public static class RunCoordinate {
                    private Integer id;
                    private Double lat; // 위도
                    private Double lon; // 경도
                    private Timestamp createdAt; // 프론트에서 좌표 생성시간을 받아야 한다
                }
            }
        }
        public static class RunBadges {
            private List<RunBadge> runBadges;
        }
    }
}
