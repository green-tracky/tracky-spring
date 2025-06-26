package com.example.tracky.runrecord;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import com.example.tracky.runrecord.DTO.AllStatsDTO;
import com.example.tracky.runrecord.DTO.RecentRunsDTO;
import com.example.tracky.runrecord.DTO.StatsDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tracky._core.error.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.runrecord.runbadge.RunBadgeRepository;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import com.example.tracky.runrecord.utils.RunRecordUtil;

import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;

import static org.springframework.data.projection.EntityProjection.ProjectionType.DTO;

@Service
@RequiredArgsConstructor
public class RunRecordService {

    private final RunRecordRepository runRecordsRepository;
    private final RunBadgeRepository runBadgeRepository;

    public void getRunRecord(Integer id) {
        // 러닝 기록 조회
        RunRecord runRecord = runRecordsRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

    }

    public RunRecordResponse.WeekDTO getActivitisWeek(LocalDate baseDate) {
        LocalDate start = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = start.plusDays(6);

        // LocalDate → LocalDateTime 타입변환
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);
        Integer totalDistanceMeters = 0; // 총 거리. 미터 단위
        Integer totalDurationSeconds = 0; // 총 시간. 초 단위

        // runBadgeList 생성
        List<RunBadge> runBadges = runBadgeRepository.findAll(); // 나중에 획득한 뱃지만 가져와야함

        List<RunBadgeResponse.DTO> runBadgeList = new ArrayList<>();
        for (RunBadge badge : runBadges) {
            runBadgeList.add(new RunBadgeResponse.DTO(badge));
        }

        // recentRunList 생성
        List<RunRecord> runRecordList = runRecordsRepository.findAllByCreatedAtBetween(startTime, endTime);

        List<RecentRunsDTO> recentRunList = new ArrayList<>();
        for (RunRecord record : runRecordList) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();

            Integer recentAvgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

            recentRunList.add(new RecentRunsDTO(record, runBadgeList, recentAvgPace));
        }

        // runStatsList 생성성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();

        int count = runRecordList.size();
        Integer statsAvgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

        StatsDTO stats = new StatsDTO(runRecord, count, statsAvgPace);

        return new RunRecordResponse.WeekDTO(stats, runBadgeList, recentRunList);
    }

    public RunRecordResponse.MonthDTO getActivitisMonth(Integer month, Integer year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // LocalDate → LocalDateTime 타입변환
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);
        Integer totalDistanceMeters = 0; // 총 거리. 미터 단위
        Integer totalDurationSeconds = 0; // 총 시간. 초 단위

        // runBadgeList 생성
        List<RunBadge> runBadges = runBadgeRepository.findAll(); // 나중에 획득한 뱃지만 가져와야함

        List<RunBadgeResponse.DTO> runBadgeList = new ArrayList<>();
        for (RunBadge badge : runBadges) {
            runBadgeList.add(new RunBadgeResponse.DTO(badge));
        }

        // recentRunList 생성
        List<RunRecord> runRecordList = runRecordsRepository.findAllByCreatedAtBetween(startTime, endTime);

        List<RecentRunsDTO> recentRunList = new ArrayList<>();
        for (RunRecord record : runRecordList) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();

            Integer recentAvgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

            recentRunList.add(new RecentRunsDTO(record, runBadgeList, recentAvgPace));
        }

        // runStatsList 생성성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();

        int count = runRecordList.size();
        Integer statsAvgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

        StatsDTO stats = new StatsDTO(runRecord, count, statsAvgPace);

        return new RunRecordResponse.MonthDTO(stats, runBadgeList, recentRunList);
    }

    public RunRecordResponse.YearDTO getActivitisYear(Integer year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        // LocalDate → LocalDateTime 타입변환
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);
        Integer totalDistanceMeters = 0; // 총 거리. 미터 단위
        Integer totalDurationSeconds = 0; // 총 시간. 초 단위

        // runBadgeList 생성
        List<RunBadge> runBadges = runBadgeRepository.findAll(); // 나중에 획득한 뱃지만 가져와야함

        List<RunBadgeResponse.DTO> runBadgeList = new ArrayList<>();
        for (RunBadge badge : runBadges) {
            runBadgeList.add(new RunBadgeResponse.DTO(badge));
        }

        // recentRunList 생성
        List<RunRecord> runRecordList = runRecordsRepository.findAllByCreatedAtBetween(startTime, endTime);

        List<RecentRunsDTO> recentRunList = new ArrayList<>();
        for (RunRecord record : runRecordList) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();

            Integer recentAvgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

            recentRunList.add(new RecentRunsDTO(record, runBadgeList, recentAvgPace));
        }

        // runStatsList 생성성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();

        int count = runRecordList.size();
        Integer statsAvgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

        StatsDTO stats = new StatsDTO(runRecord, count, statsAvgPace);

        // 둘을 포함하는 주 수 계산 (inclusive)
        long totalWeeksInYear = ChronoUnit.WEEKS.between(
                start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                end.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        ) + 1; // 양 끝 포함을 위해 +1

        double avgCountData = count > 0 ? (double) count / totalWeeksInYear : 0;
        double avgCount = Math.floor(avgCountData * 10) / 10.0;

        Integer avgDistanceMeters = count > 0 ? totalDistanceMeters / count : 0;
        Integer avgDurationSeconds = count > 0 ? totalDurationSeconds / count : 0;
        statsAvgPace = RunRecordUtil.calculatePace(avgDistanceMeters, avgDurationSeconds);

        AllStatsDTO allStats = new AllStatsDTO(avgCount, statsAvgPace, avgDistanceMeters, avgDurationSeconds);

        return new RunRecordResponse.YearDTO(stats, allStats, runBadgeList, recentRunList);
    }

    public RunRecordResponse.AllDTO getActivitisAll() {
        // 이 날짜 기준으로 조회
        List<RunRecord> runRecords = runRecordsRepository.findAllByUserIdJoin();
        List<RunBadge> runBadges = runBadgeRepository.findAll(); // 나중에 획득한 뱃지만 가져와야함

        Integer totalDistanceMeters = 0; // 총 거리. 미터 단위
        Integer totalDurationSeconds = 0; // 총 시간. 초 단위

        Integer recentDistanceMeters = 0; // 러닝별 거리. 미터 단위
        Integer recentDurationSeconds = 0; // 러닝별 시간. 초 단위

        // runBadgeList 생성
        List<RunBadgeResponse.DTO> runBadgeList = new ArrayList<>();
        for (RunBadge badge : runBadges) {
            runBadgeList.add(new RunBadgeResponse.DTO(badge));
        }

        // recentRunList 생성
        List<RecentRunsDTO> recentRunList = new ArrayList<>();
        for (RunRecord record : runRecords) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            recentDistanceMeters = record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
            recentDurationSeconds = record.getTotalDurationSeconds();

            Integer recentAvgPace = RunRecordUtil.calculatePace(recentDistanceMeters, recentDurationSeconds);

            recentRunList.add(new RecentRunsDTO(record, runBadgeList, recentAvgPace));
        }

        // runStatsList 생성성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();

        int count = recentRunList.size();
        Integer statsAvgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

        StatsDTO stats = new StatsDTO(runRecord, count, statsAvgPace);

        // 기록 중 가장 빠른 날짜
        LocalDate start = runRecords.stream()
                .map(r -> r.getCreatedAt().toLocalDateTime().toLocalDate())
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());

        // 기록 중 가장 늦은 날짜
        LocalDate end = runRecords.stream()
                .map(r -> r.getCreatedAt().toLocalDateTime().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        // 주의 시작은 월요일, 끝은 일요일로 정렬
        LocalDate adjustedStart = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate adjustedEnd = end.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 주 단위 계산 (양 끝 포함)
        long weeks = ChronoUnit.WEEKS.between(adjustedStart, adjustedEnd) + 1;

        double avgCountData = count > 0 ? (double) count / weeks : 0;
        double avgCount = Math.floor(avgCountData * 10) / 10.0;

        Integer avgDistanceMeters = count > 0 ? totalDistanceMeters / count : 0;
        Integer avgDurationSeconds = count > 0 ? totalDurationSeconds / count : 0;

        AllStatsDTO allStats = new AllStatsDTO(avgCount, statsAvgPace, avgDistanceMeters, avgDurationSeconds);

        // MainDTO
        return new RunRecordResponse.AllDTO(stats, allStats, runBadgeList, recentRunList);
    }


    /**
     * 러닝 저장
     *
     * @param user
     * @param reqDTO
     */
    @Transactional
    public RunRecordResponse.SaveDTO save(User user, RunRecordRequest.SaveDTO reqDTO) {
        // 엔티티 변환
        RunRecord runRecord = reqDTO.toEntity(user);

        // 엔티티 저장
        RunRecord runRecordPS = runRecordsRepository.save(runRecord);

        // 응답 DTO 로 변환
        return new RunRecordResponse.SaveDTO(runRecordPS);
    }

    public RunRecordResponse.DateOptionsDTO getDateOptions() {
        List<RunRecord> all = runRecordsRepository.findAllByUserIdJoin();

        Set<Integer> yearData = new HashSet<>();
        Map<Integer, Set<Integer>> monthData = new HashMap<>();
        Map<String, Set<String>> weekData = new HashMap<>();

        for (RunRecord record : all) {
            LocalDate date = record.getCreatedAt().toLocalDateTime().toLocalDate();

            int year = date.getYear();
            int month = date.getMonthValue();

            // 연도
            yearData.add(year);

            // 월
            monthData.putIfAbsent(year, new HashSet<>());
            monthData.get(year).add(month);

            // 주
            LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            String weekLabel = startOfWeek.getMonthValue() + "." + startOfWeek.getDayOfMonth()
                    + "~" + endOfWeek.getMonthValue() + "." + endOfWeek.getDayOfMonth();
            String key = year + "-" + String.format("%02d", month);

            weekData.putIfAbsent(key, new HashSet<>());
            weekData.get(key).add(weekLabel);
        }

        // DTO 생성
        RunRecordResponse.DateOptionsDTO dto = new RunRecordResponse.DateOptionsDTO();
        dto.setYears(new ArrayList<>(yearData));

        Map<Integer, List<Integer>> sortedMonthMap = new HashMap<>();
        for (Integer y : monthData.keySet()) {
            sortedMonthMap.put(y, monthData.get(y).stream().sorted().toList());
        }
        dto.setMounts(sortedMonthMap);

        LocalDate today = LocalDate.now();
        String currentMonthKey = today.getYear() + "-" + String.format("%02d", today.getMonthValue());

        Map<String, List<String>> filteredWeeksMap = new HashMap<>();
        if (weekData.containsKey(currentMonthKey)) {
            Set<String> weeks = weekData.get(currentMonthKey);
            List<String> sortedWeeks = new ArrayList<>(weeks);
            Collections.sort(sortedWeeks);
            filteredWeeksMap.put(currentMonthKey, sortedWeeks);
        }
        // 기존 방식과 동일
        dto.setWeeks(filteredWeeksMap);

        return dto;
    }
}