package com.example.tracky.runrecord;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tracky._core.error.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.runrecord.RunRecordResponse.MainPageDTO;
import com.example.tracky.runrecord.RunRecordResponse.MainPageDTO.RecentRunsDTO;
import com.example.tracky.runrecord.RunRecordResponse.StatsDTO;
import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.runrecord.runbadge.RunBadgeRepository;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import com.example.tracky.runrecord.utils.RunRecordUtil;

import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;

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

    public MainPageDTO getActivitis() {
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

            recentRunList.add(new MainPageDTO.RecentRunsDTO(record, runBadgeList, recentAvgPace));
        }

        // runStatsList 생성성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();

        int count = runRecords.size();
        Integer statsAvgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

        List<StatsDTO> statsList = List.of(new StatsDTO(runRecord, count, statsAvgPace));

        // MainDTO
        return new RunRecordResponse.MainPageDTO(statsList, runBadgeList, recentRunList);
    }

    public StatsDTO getActivitisWeek(LocalDate baseDate) {
        LocalDate start = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = start.plusDays(6);

        // LocalDate → LocalDateTime 타입변환
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        List<RunRecord> runRecordList = runRecordsRepository.findAllByCreatedAtBetween(startTime, endTime);

        // Utill 로 빼야하나? RunCount 와 avgPace 구하는 것
        Integer totalDistanceMeters = 0; // 총 거리. 미터 단위
        Integer totalDurationSeconds = 0; // 총 시간. 초 단위

        // runStatsList 생성성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();

        int count = runRecordList.size();
        Integer statsAvgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

        return new RunRecordResponse.StatsDTO(runRecord, count, statsAvgPace);
    }

    public StatsDTO getActivitisMonth(Integer month, Integer year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // LocalDate → LocalDateTime 타입변환
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        List<RunRecord> runRecordList = runRecordsRepository.findAllByCreatedAtBetween(startTime, endTime);

        // Utill 로 빼야하나? RunCount 와 avgPace 구하는 것
        Integer totalDistanceMeters = 0; // 총 거리. 미터 단위
        Integer totalDurationSeconds = 0; // 총 시간. 초 단위

        // runStatsList 생성성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();

        int count = runRecordList.size();
        Integer statsAvgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

        return new RunRecordResponse.StatsDTO(runRecord, count, statsAvgPace);
    }

    public StatsDTO getActivitisYear(Integer year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        // LocalDate → LocalDateTime 타입변환
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        List<RunRecord> runRecordList = runRecordsRepository.findAllByCreatedAtBetween(startTime, endTime);

        // Utill 로 빼야하나? RunCount 와 avgPace 구하는 것
        Integer totalDistanceMeters = 0; // 총 거리. 미터 단위
        Integer totalDurationSeconds = 0; // 총 시간. 초 단위

        // runStatsList 생성성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();

        int count = runRecordList.size();
        Integer statsAvgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

        return new RunRecordResponse.StatsDTO(runRecord, count, statsAvgPace);
    }

    public StatsDTO getActivitisAll() {
        List<RunRecord> runRecordList = runRecordsRepository.findAllByUserIdJoin();
        System.out.println(runRecordList.toString());

        // Utill 로 빼야하나? RunCount 와 avgPace 구하는 것
        List<RunRecord> runRecords = runRecordsRepository.findAllByUserIdJoin();

        Integer totalDistanceMeters = 0; // 총 거리. 미터 단위
        Integer totalDurationSeconds = 0; // 총 시간. 초 단위

        // recentRunList 생성
        List<RecentRunsDTO> recentRunList = new ArrayList<>();
        for (RunRecord record : runRecords) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
        }

        // runStatsList 생성성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();

        int count = runRecords.size();
        Integer statsAvgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

        return new RunRecordResponse.StatsDTO(runRecord, count, statsAvgPace);
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