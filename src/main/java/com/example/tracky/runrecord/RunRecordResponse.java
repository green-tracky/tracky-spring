package com.example.tracky.runrecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.tracky.runrecord.DTO.RecentRunsDTO;
import com.example.tracky.runrecord.DTO.StatsDTO;
import com.example.tracky.runrecord.picture.PictureResponse;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import com.example.tracky.runrecord.runsegment.RunSegmentResponse;
import lombok.Data;
import lombok.ToString;

public class RunRecordResponse {

    @Data
    public static class WeekDTO {
        private StatsDTO runstats;
        private List<RecentRunsDTO> recentRuns;
        private List<RunBadgeResponse.DTO> badges;

        public WeekDTO(StatsDTO runstats, List<RunBadgeResponse.DTO> badges, List<RecentRunsDTO> recentRuns) {
            this.runstats = runstats;
            this.badges = badges;
            this.recentRuns = recentRuns;
        }
    }

    @Data
    public static class MonthDTO {
        private StatsDTO runstats;
        private List<RecentRunsDTO> recentRuns;
        private List<RunBadgeResponse.DTO> badges;

        public MonthDTO(StatsDTO runstats, List<RunBadgeResponse.DTO> badges, List<RecentRunsDTO> recentRuns) {
            this.runstats = runstats;
            this.badges = badges;
            this.recentRuns = recentRuns;
        }
    }

    @Data
    public static class YearDTO {
        private StatsDTO runstats;
        private List<RecentRunsDTO> recentRuns;
        private List<RunBadgeResponse.DTO> badges;

        public YearDTO(StatsDTO runstats, List<RunBadgeResponse.DTO> badges, List<RecentRunsDTO> recentRuns) {
            this.runstats = runstats;
            this.badges = badges;
            this.recentRuns = recentRuns;
        }
    }

    @Data
    public static class AllDTO {
        private StatsDTO runstats;
        private List<RecentRunsDTO> recentRuns;
        private List<RunBadgeResponse.DTO> badges;

        public AllDTO(StatsDTO runstats, List<RunBadgeResponse.DTO> badges, List<RecentRunsDTO> recentRuns) {
            this.runstats = runstats;
            this.badges = badges;
            this.recentRuns = recentRuns;
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
