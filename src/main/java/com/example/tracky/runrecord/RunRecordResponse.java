package com.example.tracky.runrecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.tracky.runrecord.picture.PictureResponse;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import com.example.tracky.runrecord.runsegment.RunSegmentResponse;
import lombok.Data;
import lombok.ToString;

public class RunRecordResponse {

    /**
     * private Integer totalDistanceMeters;
     * <p>
     * private Integer totalDurationSeconds;
     * <p>
     * private Integer countRecode;
     * <p>
     * private String avgPace;
     * <p>
     * private List<DTO> runRecords;
     */
    @Data
    public static class MainPageDTO {
        private List<StatsDTO> runstats;
        private List<RecentRunsDTO> recentRuns;
        private List<RunBadgeResponse.DTO> badges;

        public MainPageDTO(List<StatsDTO> runstats, List<RunBadgeResponse.DTO> badges, List<RecentRunsDTO> recentRuns) {
            this.runstats = runstats;
            this.badges = badges;
            this.recentRuns = recentRuns;
        }

        @Data
        static class RecentRunsDTO {
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
    }

    @ToString
    @Data
    public static class StatsDTO {
        private Integer totalDistanceMeters; // 총 거리. 미터 단위 [StatsDTO]
        private Integer totalDurationSeconds; // 총 시간. 초 단위
        private Integer countRecode;
        private Integer avgPace;

        public StatsDTO(RunRecord runRecord, Integer countRecode,
                        Integer avgPace) {
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.countRecode = countRecode;
            this.avgPace = avgPace;
        }
    }

    @Data
    public static class DateOptionsDTO {
        private List<Integer> years = new ArrayList<>();
        private Map<Integer, List<Integer>> mounts = new HashMap<>();
        private Map<String, List<String>> weeks = new HashMap<>();
    }

    /**
     * private Integer id;
     * <p>
     * private String title;
     * <p>
     * private String memo;
     * <p>
     * private Integer calories;
     * <p>
     * private List<RunSegmentResponse.DTO> segments;
     * <p>
     * private List<PictureResponse.DTO> pictures;
     */

    /**
     * private Integer id;
     * <p>
     * private String title;
     * <p>
     * private String memo;
     * <p>
     * private Integer calories;
     * <p>
     * private List<RunSegmentResponse.DTO> segments;
     * <p>
     * private List<PictureResponse.DTO> pictures;
     */
    @Data
    public static class SaveDTO {
        private Integer id;
        private String title;
        private String memo;
        private Integer calories;
        private Integer totalDistanceMeters;
        private Integer totalDurationSeconds;
        private Integer avgPace;
        private Integer bestPace;
        private Integer userId;
        private List<RunSegmentResponse.DTO> segments;
        private List<PictureResponse.DTO> pictures;

        public SaveDTO(RunRecord runRecord) {
            this.id = runRecord.getId();
            this.title = runRecord.getTitle();
            this.memo = runRecord.getMemo();
            this.calories = runRecord.getCalories();
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.segments = runRecord.getRunSegments().stream()
                    .map(s -> new RunSegmentResponse.DTO(s))
                    .toList();
            this.pictures = runRecord.getPictures().stream()
                    .map(p -> new PictureResponse.DTO(p))
                    .toList();
            this.avgPace = (int) this.segments.stream()
                    .mapToInt(s -> s.getPace())
                    .average()
                    .orElse(0);
            this.bestPace = this.segments.stream()
                    .mapToInt(s -> s.getPace())
                    .min()
                    .orElse(0);
            this.userId = runRecord.getUser().getId();
        }

    }

}
