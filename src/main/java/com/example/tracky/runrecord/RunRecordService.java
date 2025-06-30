package com.example.tracky.runrecord;

import com.example.tracky._core.error.Enum.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchv;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvService;
import com.example.tracky.user.User;
import com.example.tracky.user.runlevel.RunLevelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import com.example.tracky.runrecord.DTO.TotalStatsDTO;
import com.example.tracky.runrecord.DTO.RecentRunsDTO;
import com.example.tracky.runrecord.DTO.AvgStatsDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.runrecord.runbadge.RunBadgeRepository;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import com.example.tracky.runrecord.utils.RunRecordUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunRecordService {

    private final RunRecordRepository runRecordRepository;
    private final RunBadgeAchvService runBadgeAchvService;
    private final RunLevelService runLevelService;
    private final RunRecordRepository runRecordsRepository;
    private final RunBadgeRepository runBadgeRepository;

    /**
     * 러닝 상세 조회
     *
     * @param id runRecordId
     * @return
     */
    public RunRecordResponse.DetailDTO getRunRecord(User user, Integer id) {
        // 1. 러닝 기록 조회
        RunRecord runRecordPS = runRecordRepository.findByIdJoin(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

        // 2. 권한 체크
        checkRunRecordAccess(user, runRecordPS);

        // 3. 러닝 응답 DTO 로 변환
        return new RunRecordResponse.DetailDTO(runRecordPS);
    }

    /**
     * 주간 러닝 활동 통계를 조회
     * <p>
     * - 기준일을 포함한 주(월~일) 단위로 거리, 시간, 획득 배지, 최근 기록 리스트 반환
     * <p>
     *
     * @param baseDate 기준 날짜
     * @return WeekDTO - 누적 통계(AvgStatsDTO), 배지 목록, 최근 러닝 기록 목록 포함
     */
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

        AvgStatsDTO stats = new AvgStatsDTO(runRecord, count, statsAvgPace);

        return new RunRecordResponse.WeekDTO(stats, runBadgeList, recentRunList);
    }

    /**
     * 월간 러닝 활동 통계를 조회
     * <p>
     * - 특정 연/월 내 기록된 러닝 정보를 기반으로 누적 통계, 배지, 최근 기록 리스트 반환
     * <p>
     *
     * @param month 조회할 월 (1~12)
     * @param year  조회할 연도
     * @return MonthDTO - 누적 통계(AvgStatsDTO), 배지 목록, 최근 러닝 기록 목록 포함
     */
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

        AvgStatsDTO stats = new AvgStatsDTO(runRecord, count, statsAvgPace);

        return new RunRecordResponse.MonthDTO(stats, runBadgeList, recentRunList);
    }

    /**
     * 연간 러닝 활동 통계를 조회
     * <p>
     * - 전체 거리/시간 기반 누적 통계 + 주간 평균 활동(평균 러닝 수, 거리 등) 반환
     * <p>
     *
     * @param year 조회할 연도
     * @return YearDTO - 누적 통계(AvgStatsDTO), 평균 통계(TotalStatsDTO), 배지 목록, 최근 기록 목록 포함
     */
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

        AvgStatsDTO stats = new AvgStatsDTO(runRecord, count, statsAvgPace);

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

        TotalStatsDTO allStats = new TotalStatsDTO(avgCount, statsAvgPace, avgDistanceMeters, avgDurationSeconds);

        return new RunRecordResponse.YearDTO(stats, allStats, runBadgeList, recentRunList);
    }

    /**
     * 전체 러닝 활동 통계를 조회
     * <p>
     * - 모든 기록을 바탕으로 누적 통계 + 주당 평균 활동 정보 반환
     * <p>
     *
     * @return AllDTO - 누적 통계(AvgStatsDTO), 평균 통계(TotalStatsDTO), 배지 목록, 전체 기록 목록 포함
     */
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

        AvgStatsDTO stats = new AvgStatsDTO(runRecord, count, statsAvgPace);

        // 기록 중 가장 빠른 날짜
        LocalDateTime start = runRecords.stream()
                .map(r -> r.getCreatedAt())
                .min(Comparator.naturalOrder()) // ⬅ compareTo 대신 이걸 사용
                .orElse(LocalDateTime.now());

        // 기록 중 가장 늦은 날짜
        LocalDateTime end = runRecords.stream()
                .map(r -> r.getCreatedAt())
                .min(Comparator.naturalOrder()) // ⬅ compareTo 대신 이걸 사용
                .orElse(LocalDateTime.now());

        // 주의 시작은 월요일, 끝은 일요일로 정렬
        LocalDateTime adjustedStart = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime adjustedEnd = end.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 주 단위 계산 (양 끝 포함)
        long weeks = ChronoUnit.WEEKS.between(adjustedStart, adjustedEnd) + 1;

        double avgCountData = count > 0 ? (double) count / weeks : 0;
        double avgCount = Math.floor(avgCountData * 10) / 10.0;

        Integer avgDistanceMeters = count > 0 ? totalDistanceMeters / count : 0;
        Integer avgDurationSeconds = count > 0 ? totalDurationSeconds / count : 0;

        TotalStatsDTO allStats = new TotalStatsDTO(avgCount, statsAvgPace, avgDistanceMeters, avgDurationSeconds);

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
        // 1. DTO를 엔티티로 변환합니다.
        RunRecord runRecord = reqDTO.toEntity(user);

        // 2. 달리기 기록 엔티티를 데이터베이스에 저장합니다.
        RunRecord runRecordPS = runRecordRepository.save(runRecord);

        // 3. 뱃지 서비스를 호출하여, 저장된 기록에 대해 획득 가능한 모든 뱃지를 확인하고 부여합니다.
        // 이 과정에서 새로 획득한 뱃지 목록을 반환받습니다.
        List<RunBadgeAchv> awardedBadges = runBadgeAchvService.checkAndAwardRunBadges(runRecordPS);

        // 4. 레벨업 서비스를 호출하여 사용자의 레벨을 업데이트합니다.
        runLevelService.updateUserLevelIfNeeded(user);

        // 5. 최종적으로, 저장된 기록과 새로 획득한 뱃지 목록을 DTO로 감싸 컨트롤러에 반환합니다.
        return new RunRecordResponse.SaveDTO(runRecordPS, awardedBadges);

    }

    /**
     * 러닝 삭제
     *
     * @param user
     * @param id   runRecordId
     * @return
     */
    @Transactional
    public void delete(User user, Integer id) {
        // 러닝 기록 조회
        RunRecord runRecordPS = runRecordRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

        // 권한 체크
        checkRunRecordAccess(user, runRecordPS);

        // 삭제
        runRecordRepository.delete(runRecordPS);
    }

    @Transactional
    public RunRecordResponse.UpdateDTO update(User user, Integer id, RunRecordRequest.UpdateDTO reqDTO) {
        // 1. 러닝 기록 조회
        RunRecord runRecordPS = runRecordRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

        // 2. 권한 체크
        checkRunRecordAccess(user, runRecordPS);

        // 3. 러닝 내용 수정
        runRecordPS.update(reqDTO);

        // 4. 응답 DTO 로 반환
        return new RunRecordResponse.UpdateDTO(runRecordPS);
    }

    /**
     * 특정 러닝 기록에 대한 사용자의 접근 권한을 확인합니다.
     * 권한이 없을 경우 ExceptionApi403 예외를 발생시킵니다.
     *
     * @param user      현재 로그인한 사용자
     * @param runRecord 검사할 러닝 기록 엔티티
     */
    private void checkRunRecordAccess(User user, RunRecord runRecord) {
        if (!runRecord.getUser().getId().equals(user.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }
    }

    /**
     * 사용자의 러닝 기록 날짜 옵션(년도, 월, 주)을 조회
     * <p></p>
     * - 연/월/주 필터링 드롭다운 등을 위한 기준 데이터 제공
     *
     * @return DateOptionsDTO - 연도 리스트, 월 리스트, 주간 라벨(Map<String, List<String>>) 포함
     */
    public RunRecordResponse.DateOptionsDTO getDateOptions() {
        List<RunRecord> all = runRecordsRepository.findAllByUserIdJoin();

        Set<Integer> yearData = new HashSet<>();
        Map<Integer, Set<Integer>> monthData = new HashMap<>();
        Map<String, Set<String>> weekData = new HashMap<>();

        for (RunRecord record : all) {
            LocalDateTime date = record.getCreatedAt();

            int year = date.getYear();
            int month = date.getMonthValue();

            // 연도
            yearData.add(year);

            // 월
            monthData.putIfAbsent(year, new HashSet<>());
            monthData.get(year).add(month);

            // 주
            LocalDateTime startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDateTime endOfWeek = startOfWeek.plusDays(6);
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