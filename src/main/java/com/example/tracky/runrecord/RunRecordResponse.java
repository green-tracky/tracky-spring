package com.example.tracky.runrecord;

import com.example.tracky.runrecord.Enum.RunPlaceEnum;
import com.example.tracky.runrecord.dto.AvgStatsDTO;
import com.example.tracky.runrecord.dto.RecentRunsDTO;
import com.example.tracky.runrecord.dto.TotalStatsDTO;
import com.example.tracky.runrecord.picture.PictureResponse;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchv;
import com.example.tracky.runrecord.runsegment.RunSegmentResponse;
import com.example.tracky.runrecord.utils.RunRecordUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunRecordResponse {

    /**
     * 주간 러닝 기록 응답 DTO
     * <p>
     * private AvgStatsDTO runstats;
     * <p>
     * private List<RecentRunsDTO> recentRuns;
     * <p>
     * private List<RunBadgeResponse.DTO> badges;
     */
    @Data
    public static class WeekDTO {
        private AvgStatsDTO avgStats;
        private List<RecentRunsDTO> recentRuns;
        private List<RunBadgeResponse.DTO> badges;
        private Map<String, List<String>> weeks = new HashMap<>();

        public WeekDTO(AvgStatsDTO avgStats, List<RunBadgeResponse.DTO> badges, List<RecentRunsDTO> recentRuns) {
            this.avgStats = avgStats;
            this.badges = badges;
            this.recentRuns = recentRuns;
        }
    }

    /**
     * 월간 러닝 기록 응답 DTO
     * <p>
     * private AvgStatsDTO runstats;
     * <p>
     * private List<RecentRunsDTO> recentRuns;
     * <p>
     * private List<RunBadgeResponse.DTO> badges;
     */
    @Data
    public static class MonthDTO {
        private AvgStatsDTO avgStats;
        private List<RecentRunsDTO> recentRuns;
        private List<RunBadgeResponse.DTO> badges;
        private List<Integer> years = new ArrayList<>();
        private Map<Integer, List<Integer>> mounts = new HashMap<>();

        public MonthDTO(AvgStatsDTO avgStats, List<RunBadgeResponse.DTO> badges, List<RecentRunsDTO> recentRuns) {
            this.avgStats = avgStats;
            this.badges = badges;
            this.recentRuns = recentRuns;
        }
    }

    /**
     * 연간 러닝 기록 응답 DTO
     * <p>
     * private AvgStatsDTO runstats;
     * <p>
     * private TotalStatsDTO allStats;
     * <p>
     * private List<RecentRunsDTO> recentRuns;
     * <p>
     * private List<RunBadgeResponse.DTO> badges;
     */
    @Data
    public static class YearDTO {
        private AvgStatsDTO avgStats;
        private TotalStatsDTO totalstats;
        private List<RecentRunsDTO> recentRuns;
        private List<RunBadgeResponse.DTO> badges;
        private List<Integer> years = new ArrayList<>();

        public YearDTO(AvgStatsDTO avgStats, TotalStatsDTO totalstats, List<RunBadgeResponse.DTO> badges, List<RecentRunsDTO> recentRuns) {
            this.avgStats = avgStats;
            this.totalstats = totalstats;
            this.badges = badges;
            this.recentRuns = recentRuns;
        }
    }

    /**
     * 전체 러닝 기록 응답 DTO
     * <p>
     * private AvgStatsDTO runstats;
     * <p>
     * private TotalStatsDTO allStats;
     * <p>
     * private List<RecentRunsDTO> recentRuns;
     * <p>
     * private List<RunBadgeResponse.DTO> badges;
     */
    @Data
    public static class AllDTO {
        private AvgStatsDTO avgStats;
        private TotalStatsDTO totalstats;
        private List<RecentRunsDTO> recentRuns;
        private List<RunBadgeResponse.DTO> badges;

        public AllDTO(AvgStatsDTO avgStats, TotalStatsDTO totalstats, List<RunBadgeResponse.DTO> badges, List<RecentRunsDTO> recentRuns) {
            this.avgStats = avgStats;
            this.totalstats = totalstats;
            this.badges = badges;
            this.recentRuns = recentRuns;
        }
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
    @Data
    public static class SaveDTO {
        private Integer id;
        private String title;
        private String memo;
        private Integer calories;
        private Integer totalDistanceMeters; // 러닝 총 이동거리
        private Integer totalDurationSeconds; // 러닝 총 시간
        private Integer avgPace; // 평균 페이스
        private Integer bestPace; // 최고 페이스. 숫자가 낮아야 함
        private List<RunSegmentResponse.DTO> segments;
        private List<PictureResponse.DTO> pictures;
        private LocalDateTime createdAt;
        private Integer userId;
        private List<RunBadgeResponse.DTO> badges;

        public SaveDTO(RunRecord runRecord, List<RunBadgeAchv> awardedBadges) {
            this.id = runRecord.getId();
            this.title = runRecord.getTitle();
            this.memo = runRecord.getMemo();
            this.calories = runRecord.getCalories();
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.createdAt = runRecord.getCreatedAt();
            this.segments = runRecord.getRunSegments().stream()
                    .map(s -> new RunSegmentResponse.DTO(s))
                    .toList();
            this.pictures = runRecord.getPictures().stream()
                    .map(p -> new PictureResponse.DTO(p))
                    .toList();
            this.avgPace = runRecord.getAvgPace();
            this.bestPace = runRecord.getBestPace();
            this.userId = runRecord.getUser().getId();

            // 전달받은 뱃지 획득 엔티티 목록을 DTO 목록으로 변환
            this.badges = awardedBadges.stream()
                    .map(ba -> new RunBadgeResponse.DTO(ba))
                    .toList();
        }

    }

    @Data
    public static class DetailDTO {
        private Integer id;
        private String title;
        private String memo;
        private Integer calories;
        private Integer totalDistanceMeters; // 러닝 총 이동거리
        private Integer totalDurationSeconds; // 러닝 총 시간
        private Integer elapsedTimeInSeconds; // 러닝 총 경과시간
        private Integer avgPace;
        private Integer bestPace;
        private Integer userId;
        private List<RunSegmentResponse.DTO> segments;
        private List<PictureResponse.DTO> pictures;
        private LocalDateTime createdAt;
        private Integer intensity; // 러닝 강도
        private RunPlaceEnum place; // 러닝 장소

        public DetailDTO(RunRecord runRecord) {
            this.id = runRecord.getId();
            this.title = runRecord.getTitle();
            this.memo = runRecord.getMemo();
            this.calories = runRecord.getCalories();
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.elapsedTimeInSeconds = RunRecordUtil.calculateElapsedTimeInSeconds(runRecord.getRunSegments());
            this.createdAt = runRecord.getCreatedAt();
            this.segments = runRecord.getRunSegments().stream()
                    .map(s -> new RunSegmentResponse.DTO(s))
                    .toList();
            this.pictures = runRecord.getPictures().stream()
                    .map(p -> new PictureResponse.DTO(p))
                    .toList();
            this.avgPace = runRecord.getAvgPace();
            this.bestPace = runRecord.getBestPace();
            this.userId = runRecord.getUser().getId();
            this.intensity = runRecord.getIntensity();
            this.place = runRecord.getPlace();
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

    @Data
    public static class UpdateDTO {
        private Integer id;
        private String title;
        private String memo;
        private Integer calories;
        private Integer totalDistanceMeters; // 러닝 총 이동거리
        private Integer totalDurationSeconds; // 러닝 총 시간
        private Integer elapsedTimeInSeconds; // 러닝 총 경과시간
        private Integer avgPace;
        private Integer bestPace;
        private List<RunSegmentResponse.DTO> segments;
        private List<PictureResponse.DTO> pictures;
        private LocalDateTime createdAt;
        private Integer userId;
        private Integer intensity; // 러닝 강도
        private RunPlaceEnum place; // 러닝 장소

        public UpdateDTO(RunRecord runRecord) {
            this.id = runRecord.getId();
            this.title = runRecord.getTitle();
            this.memo = runRecord.getMemo();
            this.calories = runRecord.getCalories();
            this.totalDistanceMeters = runRecord.getTotalDistanceMeters();
            this.totalDurationSeconds = runRecord.getTotalDurationSeconds();
            this.elapsedTimeInSeconds = RunRecordUtil.calculateElapsedTimeInSeconds(runRecord.getRunSegments());
            this.createdAt = runRecord.getCreatedAt();
            this.segments = runRecord.getRunSegments().stream()
                    .map(s -> new RunSegmentResponse.DTO(s))
                    .toList();
            this.pictures = runRecord.getPictures().stream()
                    .map(p -> new PictureResponse.DTO(p))
                    .toList();
            this.avgPace = runRecord.getAvgPace();
            this.bestPace = runRecord.getBestPace();
            this.userId = runRecord.getUser().getId();
            this.intensity = runRecord.getIntensity();
            this.place = runRecord.getPlace();
        }
    }

}
