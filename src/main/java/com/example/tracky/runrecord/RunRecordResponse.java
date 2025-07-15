package com.example.tracky.runrecord;

import com.example.tracky._core.enums.RunPlaceTypeEnum;
import com.example.tracky.runrecord.dto.*;
import com.example.tracky.runrecord.pictures.PictureResponse;
import com.example.tracky.runrecord.runbadges.RunBadgeResponse;
import com.example.tracky.runrecord.runbadges.runbadgeachvs.RunBadgeAchv;
import com.example.tracky.runrecord.runsegments.RunSegmentResponse;
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
     */
    @Data
    public static class WeekDTO {
        private AvgStatsDTO avgStats;
        private List<RecentRunsDTO> recentRuns;
        private List<AchievementHistoryItemDTO> achievementHistory;
        private RunLevelDTO runLevel;
        private Map<String, List<String>> weeks = new HashMap<>();

        public WeekDTO(AvgStatsDTO avgStats, List<AchievementHistoryItemDTO> achievementHistory, List<RecentRunsDTO> recentRuns, RunLevelDTO runLevel) {
            this.avgStats = avgStats;
            this.achievementHistory = achievementHistory;
            this.recentRuns = recentRuns;
            this.runLevel = runLevel;
        }
    }

    /**
     * 월간 러닝 기록 응답 DTO
     */
    @Data
    public static class MonthDTO {
        private AvgStatsDTO avgStats;
        private List<RecentRunsDTO> recentRuns;
        private List<AchievementHistoryItemDTO> achievementHistory;
        private RunLevelDTO runLevel;
        private List<Integer> years = new ArrayList<>();
        private Map<Integer, List<Integer>> mounts = new HashMap<>();

        public MonthDTO(AvgStatsDTO avgStats, List<AchievementHistoryItemDTO> achievementHistory, List<RecentRunsDTO> recentRuns, RunLevelDTO runLevel) {
            this.avgStats = avgStats;
            this.achievementHistory = achievementHistory;
            this.recentRuns = recentRuns;
            this.runLevel = runLevel;
        }
    }

    /**
     * 연간 러닝 기록 응답 DTO
     */
    @Data
    public static class YearDTO {
        private AvgStatsDTO avgStats;
        private TotalStatsDTO totalStats;
        private List<RecentRunsDTO> recentRuns;
        private List<AchievementHistoryItemDTO> achievementHistory;
        private RunLevelDTO runLevel;
        private List<Integer> years = new ArrayList<>();

        public YearDTO(AvgStatsDTO avgStats, TotalStatsDTO totalStats, List<AchievementHistoryItemDTO> achievementHistory, List<RecentRunsDTO> recentRuns, RunLevelDTO runLevel) {
            this.avgStats = avgStats;
            this.totalStats = totalStats;
            this.achievementHistory = achievementHistory;
            this.recentRuns = recentRuns;
            this.runLevel = runLevel;
        }
    }

    /**
     * 전체 러닝 기록 응답 DTO
     */
    @Data
    public static class AllDTO {
        private AvgStatsDTO avgStats;
        private TotalStatsDTO totalStats;
        private List<RecentRunsDTO> recentRuns;
        private List<AchievementHistoryItemDTO> achievementHistory;
        private RunLevelDTO runLevel;

        public AllDTO(AvgStatsDTO avgStats, TotalStatsDTO totalStats, List<AchievementHistoryItemDTO> achievementHistory, List<RecentRunsDTO> recentRuns, RunLevelDTO runLevel) {
            this.avgStats = avgStats;
            this.totalStats = totalStats;
            this.achievementHistory = achievementHistory;
            this.recentRuns = recentRuns;
            this.runLevel = runLevel;
        }
    }

    /**
     * 연도-월 단위로 그룹핑된 러닝 기록을 담는 DTO
     */
    @Data
    public static class RecentOneDTO {
        private LocalDateTime yearMonth; // 예: "2025-06-01 00:00:00"
        private AvgStatsDTO avgStats;
        private List<RecentRunsDTO> recentRuns;

        public RecentOneDTO(LocalDateTime yearMonth, AvgStatsDTO avgStats, List<RecentRunsDTO> recentRuns) {
            this.yearMonth = yearMonth;
            this.avgStats = avgStats;
            this.recentRuns = recentRuns;
        }
    }

    /**
     * 연도-월별로 그룹화된 러닝 활동 목록 전체를 담는 DTO
     */
    @Data
    public static class GroupedRecentListDTO {
        private List<RecentOneDTO> groupedRecentList;
        private PageDTO page;

        public GroupedRecentListDTO(List<RecentOneDTO> groupedRecentList, PageDTO page) {
            this.groupedRecentList = groupedRecentList;
            this.page = page;
        }
    }

    /**
     * 기본 리스트 형태로 정렬된 최근 러닝 기록 리스트 DTO
     */
    @Data
    public static class FlatRecentListDTO {
        private List<RecentRunsDTO> recentList;
        private PageDTO page;

        public FlatRecentListDTO(List<RecentRunsDTO> recentList, PageDTO page) {
            this.recentList = recentList;
            this.page = page;
        }
    }

    @Data
    public static class SaveDTO {
        private Integer id;
        private String title;
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

        public SaveDTO(RunRecord runRecord, List<RunBadgeAchv> runBadgeAchvs) {
            this.id = runRecord.getId();
            this.title = runRecord.getTitle();
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
            this.badges = runBadgeAchvs.stream()
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
        private RunPlaceTypeEnum place; // 러닝 장소

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
        private Integer intensity; // 러닝 강도
        private RunPlaceTypeEnum place; // 러닝 장소

        public UpdateDTO(RunRecord runRecord) {
            this.id = runRecord.getId();
            this.title = runRecord.getTitle();
            this.memo = runRecord.getMemo();
            this.intensity = runRecord.getIntensity();
            this.place = runRecord.getPlace();
        }
    }

    @Data
    public static class SimpleDTO {
        private Integer id;
        private String title;
        private LocalDateTime createdAt;

        public SimpleDTO(RunRecord runRecord) {
            this.id = runRecord.getId();
            this.title = runRecord.getTitle();
            this.createdAt = runRecord.getCreatedAt();
        }
    }

}
