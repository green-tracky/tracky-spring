package com.example.tracky.runrecord;

import com.example.tracky._core.constant.Constant;
import com.example.tracky._core.error.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.runrecord.dto.*;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchv;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvRepository;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvService;
import com.example.tracky.runrecord.utils.RunRecordUtil;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.runlevel.RunLevel;
import com.example.tracky.user.runlevel.RunLevelRepository;
import com.example.tracky.user.runlevel.RunLevelService;
import com.example.tracky.user.runlevel.utils.RunLevelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class RunRecordService {

    private final RunRecordRepository runRecordRepository;
    private final RunBadgeAchvService runBadgeAchvService;
    private final RunLevelService runLevelService;
    private final RunRecordRepository runRecordsRepository;
    private final RunBadgeAchvRepository runBadgeAchvRepository;
    private final UserRepository userRepository;
    private final RunLevelRepository runLevelRepository;

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
     * @param user     현재 사용자 정보
     * @param baseDate 기준 날짜
     * @param before   기준일로부터 몇 주 전을 조회할 것인지 (0 = 이번 주, 1 = 저번 주 등)
     * @return WeekDTO - 누적 통계(AvgStatsDTO), 배지 목록, 최근 러닝 기록 목록 포함
     */
    public RunRecordResponse.WeekDTO getActivitiesWeek(User user, LocalDate baseDate, Integer before) {
        // 1. 기준 주 계산
        LocalDate targetDate = baseDate.minusWeeks(before);
        LocalDate start = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = start.plusDays(6);
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        // 2. 주간 기록 조회
        List<RunRecord> runRecordList = runRecordsRepository.findAllByCreatedAtBetween(user.getId(), startTime, endTime);
        // 📌 2. 해당 주간의 기록 조회 및 총 거리/시간 계산
        List<RunRecord> runRecords = runRecordsRepository.findAllByCreatedAtBetween(user.getId(), startTime, endTime);
        Integer totalDistanceMeters = 0;
        Integer totalDurationSeconds = 0;
        for (RunRecord record : runRecords) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
        }

        // 3. 거리/시간 합산 및 DTO 생성
        AvgStatsDTO avgStats = RunRecordUtil.avgStats(runRecords, totalDistanceMeters, totalDurationSeconds);

        // 4. 배지 리스트
        List<RunBadgeResponse.DTO> runBadgeList = runBadgeAchvRepository.findByUserIdJoin(user.getId()).stream()
                .map(RunBadgeResponse.DTO::new).toList();

        // 5. 최근 3개 러닝 기록 + DTO 변환
        List<RunRecord> recentRunRecords = runRecordsRepository.findTop3ByUserIdOrderByCreatedAtJoinBadgeAchv(user.getId());
        List<RecentRunsDTO> recentRunList = recentRunRecords.stream()
                .map(r -> new RecentRunsDTO(r))
                .toList();

        // 6. 주차 라벨 생성
        Map<String, Set<String>> weeksMap = new HashMap<>();
        for (RunRecord record : runRecordsRepository.findAllByUserId(user.getId())) {
            LocalDate date = record.getCreatedAt().toLocalDate();
            int year = date.getYear();
            int month = date.getMonthValue();
            LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            String weekLabel = startOfWeek.getMonthValue() + "." + startOfWeek.getDayOfMonth() + "~" + endOfWeek.getMonthValue() + "." + endOfWeek.getDayOfMonth();
            String key = year + "-" + String.format("%02d", month);
            weeksMap.computeIfAbsent(key, k -> new HashSet<>()).add(weekLabel);
        }

        // 7. 주차 라벨 정렬 및 '이번주/저번주' 라벨링
        String baseYearMonth = baseDate.getYear() + "-" + String.format("%02d", baseDate.getMonthValue());
        Map<String, List<String>> sortedWeeksMap = new HashMap<>();
        if (weeksMap.containsKey(baseYearMonth)) {
            List<String> sortedWeeks = weeksMap.get(baseYearMonth).stream()
                    .sorted(Comparator.comparing(label -> {
                        String[] startEnd = label.split("~")[0].split("\\.");
                        return LocalDate.of(baseDate.getYear(), Integer.parseInt(startEnd[0]), Integer.parseInt(startEnd[1]));
                    }))
                    .map(label -> {
                        LocalDate today = LocalDate.now();
                        LocalDate thisWeekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                        LocalDate lastWeekStart = thisWeekStart.minusWeeks(1);
                        String[] startParts = label.split("~")[0].split("\\.");
                        LocalDate weekStart = LocalDate.of(today.getYear(), Integer.parseInt(startParts[0]), Integer.parseInt(startParts[1]));
                        if (weekStart.equals(thisWeekStart)) return "이번주";
                        else if (weekStart.equals(lastWeekStart)) return "저번주";
                        else return label;
                    })
                    .toList();
            sortedWeeksMap.put(baseYearMonth, sortedWeeks);
        }

        // 8. 레벨 정보 조회
        RunLevel currentLevelPS = userRepository.findByIdJoin(user.getId()).orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND)).getRunLevel();
        List<RunLevel> runLevelsPS = runLevelRepository.findAllByOrderBySortOrderAsc();
        Integer totalDistance = runRecordRepository.findTotalDistanceByUserId(user.getId());
        Integer distanceToNextLevel = RunLevelUtil.getDistanceToNextLevel(currentLevelPS, runLevelsPS, totalDistance);
        RunLevelDTO runLevel = new RunLevelDTO(currentLevelPS, totalDistance, distanceToNextLevel);

        // 9. DTO 반환
        RunRecordResponse.WeekDTO weekDTO = new RunRecordResponse.WeekDTO(avgStats, runBadgeList, recentRunList, runLevel);
        weekDTO.setWeeks(sortedWeeksMap);
        return weekDTO;
    }

    /**
     * 월간 러닝 활동 통계를 조회
     * <p>
     * - 특정 연/월 내 기록된 러닝 정보를 기반으로 누적 통계, 배지, 최근 기록 리스트 반환
     * <p>
     *
     * @param user  현재 사용자 정보
     * @param month 조회할 월 (1~12)
     * @param year  조회할 연도
     * @return MonthDTO - 누적 통계(AvgStatsDTO), 배지 목록, 최근 러닝 기록 목록 포함
     */
    public RunRecordResponse.MonthDTO getActivitiesMonth(User user, Integer month, Integer year) {
        // 1. 해당 월의 시작/끝 날짜 및 시간 계산
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        // 2. 해당 기간의 기록 조회 및 거리/시간 합산
        List<RunRecord> runRecordList = runRecordsRepository.findAllByCreatedAtBetween(user.getId(), startTime, endTime);
        int totalDistanceMeters = 0;
        int totalDurationSeconds = 0;
        for (RunRecord record : runRecordList) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
        }

        // 3. 누적 통계 DTO 생성
        AvgStatsDTO avgStats = RunRecordUtil.avgStats(runRecordList, totalDistanceMeters, totalDurationSeconds);

        // 4. 획득한 배지 조회
        List<RunBadgeAchv> runBadges = runBadgeAchvRepository.findByUserIdJoin(user.getId());
        List<RunBadgeResponse.DTO> runBadgeList = new ArrayList<>();
        for (RunBadgeAchv badge : runBadges) {
            runBadgeList.add(new RunBadgeResponse.DTO(badge));
        }

        // 5. 최근 러닝 3개 변환
        List<RunRecord> recentRunRecords = runRecordsRepository.findTop3ByUserIdOrderByCreatedAtJoinBadgeAchv(user.getId());
        List<RecentRunsDTO> recentRunList = recentRunRecords.stream()
                .map(r -> new RecentRunsDTO(r))
                .toList();

        // 6. 기록이 있는 월/연도 목록 구성
        List<RunRecord> runRecordAll = runRecordsRepository.findAllByUserId(user.getId());
        Set<Integer> yearSet = new HashSet<>();
        Map<Integer, Set<Integer>> monthsMap = new HashMap<>();
        for (RunRecord record : runRecordAll) {
            LocalDate date = record.getCreatedAt().toLocalDate();
            int recordYear = date.getYear();
            int recordMonth = date.getMonthValue();
            yearSet.add(recordYear);
            monthsMap.computeIfAbsent(recordYear, k -> new HashSet<>()).add(recordMonth);
        }

        // 7. 레벨 관련 정보 계산
        RunLevel currentLevelPS = userRepository.findByIdJoin(user.getId())
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND))
                .getRunLevel();
        List<RunLevel> runLevelsPS = runLevelRepository.findAllByOrderBySortOrderAsc();
        Integer totalDistance = runRecordRepository.findTotalDistanceByUserId(user.getId());
        Integer distanceToNextLevel = RunLevelUtil.getDistanceToNextLevel(currentLevelPS, runLevelsPS, totalDistance);
        RunLevelDTO RunLevel = new RunLevelDTO(currentLevelPS, totalDistance, distanceToNextLevel);

        // 8. 최종 DTO 구성
        RunRecordResponse.MonthDTO monthDTO = new RunRecordResponse.MonthDTO(avgStats, runBadgeList, recentRunList, RunLevel);
        monthDTO.setYears(yearSet.stream().sorted().toList());

        Map<Integer, List<Integer>> sortedMonthMap = new HashMap<>();
        for (Integer y : monthsMap.keySet()) {
            sortedMonthMap.put(y, monthsMap.get(y).stream().sorted().toList());
        }
        monthDTO.setMounts(sortedMonthMap);

        return monthDTO;
    }

    /**
     * 연간 러닝 활동 통계를 조회
     * <p>
     * - 전체 거리/시간 기반 누적 통계 + 주간 평균 활동(평균 러닝 수, 거리 등) 반환
     * <p>
     *
     * @param user 현재 사용자 정보
     * @param year 조회할 연도
     * @return YearDTO - 누적 통계(AvgStatsDTO), 평균 통계(TotalStatsDTO), 배지 목록, 최근 기록 목록 포함
     */
    public RunRecordResponse.YearDTO getActivitiesYear(User user, Integer year) {
        // 1. 연도 시작/끝 날짜 계산
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        // 2. 해당 연도의 기록 조회 및 총 거리/시간 계산
        List<RunRecord> runRecordList = runRecordsRepository.findAllByCreatedAtBetween(user.getId(), startTime, endTime);
        int totalDistanceMeters = 0;
        int totalDurationSeconds = 0;
        for (RunRecord record : runRecordList) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
        }


        // 3. 누적 통계용 AvgStatsDTO 생성
        AvgStatsDTO avgStats = RunRecordUtil.avgStats(runRecordList, totalDistanceMeters, totalDurationSeconds);

        // 4. 획득한 배지 조회
        List<RunBadgeAchv> runBadges = runBadgeAchvRepository.findByUserIdJoin(user.getId());
        List<RunBadgeResponse.DTO> runBadgeList = new ArrayList<>();
        for (RunBadgeAchv badge : runBadges) {
            runBadgeList.add(new RunBadgeResponse.DTO(badge));
        }

        // 5. 최근 3개의 러닝 기록 조회 및 DTO 변환
        List<RunRecord> recentRunRecords = runRecordsRepository.findTop3ByUserIdOrderByCreatedAtJoinBadgeAchv(user.getId());
        List<RecentRunsDTO> recentRunList = recentRunRecords.stream()
                .map(r -> new RecentRunsDTO(r))
                .toList();

        // 6. 주간 평균 통계 계산
        long totalWeeksInYear = ChronoUnit.WEEKS.between(
                start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                end.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        ) + 1;
        double avgCountData = avgStats.getRecodeCount() > 0 ? (double) avgStats.getRecodeCount() / totalWeeksInYear : 0;
        double avgCount = Math.floor(avgCountData * 10) / 10.0;
        Integer avgDistanceMeters = avgStats.getRecodeCount() > 0 ? totalDistanceMeters / avgStats.getRecodeCount() : 0;
        Integer avgDurationSeconds = avgStats.getRecodeCount() > 0 ? totalDurationSeconds / avgStats.getRecodeCount() : 0;
        Integer statsAvgPace = RunRecordUtil.calculatePace(avgDistanceMeters, avgDurationSeconds);
        TotalStatsDTO allStats = new TotalStatsDTO(avgCount, statsAvgPace, avgDistanceMeters, avgDurationSeconds);

        // 7. 기록이 있는 연도 목록 추출
        List<RunRecord> runRecordAll = runRecordsRepository.findAllByUserId(user.getId());
        Set<Integer> yearData = new HashSet<>();
        for (RunRecord record : runRecordAll) {
            yearData.add(record.getCreatedAt().getYear());
        }

        // 8. 현재 레벨 및 누적 거리, 다음 레벨까지 남은 거리 계산
        RunLevel currentLevelPS = userRepository.findByIdJoin(user.getId())
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND))
                .getRunLevel();
        List<RunLevel> runLevelsPS = runLevelRepository.findAllByOrderBySortOrderAsc();
        Integer totalDistance = runRecordRepository.findTotalDistanceByUserId(user.getId());
        Integer distanceToNextLevel = RunLevelUtil.getDistanceToNextLevel(currentLevelPS, runLevelsPS, totalDistance);
        RunLevelDTO RunLevel = new RunLevelDTO(currentLevelPS, totalDistance, distanceToNextLevel);

        // 9. 최종 DTO 생성 및 반환
        RunRecordResponse.YearDTO yearDTO = new RunRecordResponse.YearDTO(avgStats, allStats, runBadgeList, recentRunList, RunLevel);
        yearDTO.setYears(new ArrayList<>(yearData));

        return yearDTO;
    }

    /**
     * 전체 러닝 활동 통계를 조회
     * <p>
     * - 모든 기록을 바탕으로 누적 통계 + 주당 평균 활동 정보 반환
     * <p>
     *
     * @param user 현재 사용자 정보
     * @return AllDTO - 누적 통계(AvgStatsDTO), 평균 통계(TotalStatsDTO), 배지 목록, 전체 기록 목록 포함
     */
    public RunRecordResponse.AllDTO getActivitiesAll(User user) {
        // 1. 전체 러닝 기록 조회
        List<RunRecord> runRecords = runRecordsRepository.findAllByUserId(user.getId());

        // 2. 총 거리와 총 소요 시간 계산
        int totalDistanceMeters = 0;
        int totalDurationSeconds = 0;
        for (RunRecord record : runRecords) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
        }

        // 3. 누적 통계 생성 (러닝 수, 평균 페이스 등)
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();
        int statsCount = runRecords.size();
        Integer avgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);
        AvgStatsDTO stats = new AvgStatsDTO(runRecord, statsCount, avgPace);

        // 4. 배지 목록 조회
        List<RunBadgeAchv> runBadges = runBadgeAchvRepository.findByUserIdJoin(user.getId());
        List<RunBadgeResponse.DTO> runBadgeList = new ArrayList<>();
        for (RunBadgeAchv badge : runBadges) {
            runBadgeList.add(new RunBadgeResponse.DTO(badge));
        }

        // 5. 최근 3개의 러닝 기록 조회
        List<RunRecord> recentRunRecords = runRecordsRepository.findTop3ByUserIdOrderByCreatedAtJoinBadgeAchv(user.getId());
        List<RecentRunsDTO> recentRunList = recentRunRecords.stream()
                .map(r -> new RecentRunsDTO(r))
                .toList();

        // 6. 현재 레벨, 총 거리, 다음 레벨까지 거리 계산
        RunLevel currentLevelPS = userRepository.findByIdJoin(user.getId())
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND))
                .getRunLevel();
        List<RunLevel> runLevelsPS = runLevelRepository.findAllByOrderBySortOrderAsc();
        Integer totalDistance = runRecordRepository.findTotalDistanceByUserId(user.getId());
        Integer distanceToNextLevel = RunLevelUtil.getDistanceToNextLevel(currentLevelPS, runLevelsPS, totalDistance);
        RunLevelDTO RunLevel = new RunLevelDTO(currentLevelPS, totalDistance, distanceToNextLevel);

        // 7. 전체 주 수 계산 (최초 기록 ~ 마지막 기록 사이의 월~일 기준 주차 수)
        if (runRecords.isEmpty()) {
            TotalStatsDTO allStats = new TotalStatsDTO(0.0, 0, 0, 0);
            return new RunRecordResponse.AllDTO(stats, allStats, runBadgeList, recentRunList, RunLevel);
        }

        LocalDateTime start = runRecords.stream().map(RunRecord::getCreatedAt).min(Comparator.naturalOrder()).orElse(LocalDateTime.now());
        LocalDateTime end = runRecords.stream().map(RunRecord::getCreatedAt).max(Comparator.naturalOrder()).orElse(LocalDateTime.now());
        LocalDateTime adjustedStart = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime adjustedEnd = end.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        long weeks = ChronoUnit.WEEKS.between(adjustedStart, adjustedEnd) + 1;

        // 8. 평균 통계 생성
        double avgCountData = statsCount > 0 ? (double) statsCount / weeks : 0;
        double avgCount = Math.floor(avgCountData * 10) / 10.0;
        Integer avgDistanceMeters = statsCount > 0 ? totalDistanceMeters / statsCount : 0;
        Integer avgDurationSeconds = statsCount > 0 ? totalDurationSeconds / statsCount : 0;
        Integer statsAvgPace = RunRecordUtil.calculatePace(avgDistanceMeters, avgDurationSeconds);
        TotalStatsDTO allStats = new TotalStatsDTO(avgCount, statsAvgPace, avgDistanceMeters, avgDurationSeconds);

        // 9. 최종 DTO 반환
        return new RunRecordResponse.AllDTO(stats, allStats, runBadgeList, recentRunList, RunLevel);
    }

    /**
     * 최근 러닝 기록을 연도-월 기준으로 그룹핑하여 통계 반환
     * <p>
     * - 사용자 ID로 전체 기록을 조회한 뒤 정렬 조건(order)에 따라 정렬
     * <p>
     * - 각 기록을 YearMonth 단위로 그룹핑
     * <p>
     * - 각 그룹에 대해 거리, 시간, 평균 페이스 등 통계 생성
     * <p>
     * - RecentOneDTO 리스트를 모아 GroupedRecentListDTO 로 반환
     * <p>
     *
     * @param user  통계를 조회할 사용자 정보
     * @param order 정렬 기준 (latest, oldest)
     * @param year  기준 연도
     * @return GroupedRecentListDTO - 연도/월별 러닝 통계 + 상세 기록 리스트 포함 DTO
     */
    public RunRecordResponse.GroupedRecentListDTO getGroupedActivities(User user, String order, Integer year, Integer page) {
        List<RunRecord> runRecords = runRecordsRepository.findAllByUserId(user.getId());
        List<RunRecord> filteredAndSorted = new ArrayList<>();

        if (year != null) {
            for (RunRecord record : runRecords) {
                if (record.getCreatedAt().getYear() == year) {
                    filteredAndSorted.add(record);
                }
            }
        } else {
            filteredAndSorted.addAll(runRecords);
        }

        // 정렬
        Comparator<RunRecord> comparator = (r1, r2) -> {
            if ("oldest".equals(order)) {
                return r1.getCreatedAt().compareTo(r2.getCreatedAt()); // 오름차순
            } else {
                return r2.getCreatedAt().compareTo(r1.getCreatedAt()); // 내림차순
            }
        };
        filteredAndSorted.sort(comparator);

        runRecords = filteredAndSorted;

        // 1. YearMonth 기준으로 그룹핑
        Comparator<YearMonth> ymComparator = "oldest".equals(order) ? Comparator.naturalOrder() : Comparator.reverseOrder();

        Map<YearMonth, List<RunRecord>> groupedByMonth = new TreeMap<>(ymComparator);

        for (RunRecord record : runRecords) {
            YearMonth ym = YearMonth.from(record.getCreatedAt());
            groupedByMonth.computeIfAbsent(ym, k -> new ArrayList<>()).add(record);
        }

        // ✅ 2. Entry 리스트로 변환 및 페이징 처리
        List<Map.Entry<YearMonth, List<RunRecord>>> groupedEntries = new ArrayList<>(groupedByMonth.entrySet());
        int totalCount = groupedEntries.size();
        int size = Constant.RUN_LIST_FETCH_SIZE;
        int currentPage = Math.max(1, page);
        int fromIndex = (currentPage - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalCount);
        List<RunRecordResponse.RecentOneDTO> groupRecentList = new ArrayList<>();

        for (int i = fromIndex; i < toIndex; i++) {
            Map.Entry<YearMonth, List<RunRecord>> entry = groupedEntries.get(i);
            List<RunRecord> records = entry.getValue();

            // 거리, 시간 합산
            int totalDistance = 0;
            int totalDuration = 0;
            for (RunRecord r : records) {
                totalDistance += r.getTotalDistanceMeters();
                totalDuration += r.getTotalDurationSeconds();
            }
            int count = records.size();
            int avgPace = RunRecordUtil.calculatePace(totalDistance, totalDuration);

            // AvgStatsDTO 생성용 dummy RunRecord
            RunRecord dummy = RunRecord.builder()
                    .totalDistanceMeters(totalDistance)
                    .totalDurationSeconds(totalDuration)
                    .build();
            AvgStatsDTO avgStats = new AvgStatsDTO(dummy, count, avgPace);

            // RecentRunsDTO 리스트
            List<RecentRunsDTO> recents = new ArrayList<>();
            for (RunRecord r : records) {
                recents.add(new RecentRunsDTO(r));
            }

            // 기준일을 첫 기록의 createdAt 기준으로 설정
            LocalDateTime baseDateTime = recents.get(0).getCreatedAt();
            LocalDateTime dateTime = YearMonth.from(baseDateTime).atDay(1).atStartOfDay();

            groupRecentList.add(new RunRecordResponse.RecentOneDTO(dateTime, avgStats, recents));
        }

        PageDTO pageing = new PageDTO(totalCount, currentPage);


        return new RunRecordResponse.GroupedRecentListDTO(groupRecentList, pageing);
    }

    /**
     * 최근 러닝 기록을 거리/페이스 정렬 기준의 전체 러닝 기록 리스트 반환
     * <p>
     * - 사용자 ID로 모든 러닝 기록을 조회하고, 정렬 기준(order)에 따라 정렬
     * <p>
     * - 각 기록을 RecentRunsDTO로 변환하여 리스트 구성
     * <p>
     * - 그룹핑 없이 평면(flat) 리스트 형태로 반환
     *
     * @param user  사용자 정보
     * @param order 정렬 기준 (distance-asc, distance-desc, pace-asc, pace-desc)
     * @param year  기준 연도
     * @return FlatRecentListDTO - 정렬된 러닝 기록 리스트 포함 DTO
     */
    public RunRecordResponse.FlatRecentListDTO getFlatActivities(User user, String order, Integer year, Integer page) {
        List<RunRecord> runRecords;
        switch (order) {
            case "distance-desc" ->
                    runRecords = runRecordsRepository.findAllByUserIdOrderByDistanceDesc(user.getId(), page);
            case "distance-asc" ->
                    runRecords = runRecordsRepository.findAllByUserIdOrderByDistanceAsc(user.getId(), page);
            case "pace-desc" -> runRecords = runRecordsRepository.findAllByUserIdOrderByAvgPaceDesc(user.getId(), page);
            case "pace-asc" -> runRecords = runRecordsRepository.findAllByUserIdOrderByAvgPaceAsc(user.getId(), page);
            default ->
                    runRecords = runRecordsRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId(), page); // 기본: 최신순
        }

        if (year != null) {
            runRecords = runRecords.stream()
                    .filter(r -> r.getCreatedAt().getYear() == year)
                    .collect(Collectors.toList());
        }

        List<RecentRunsDTO> recentRuns = runRecords.stream()
                .map(r -> new RecentRunsDTO(r))
                .toList();

        // 3. paging
        Long totalcount = runRecordRepository.totalCount(user.getId());

        PageDTO pageing = new PageDTO(totalcount.intValue(), page);

        return new RunRecordResponse.FlatRecentListDTO(recentRuns, pageing);
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
        List<RunBadgeAchv> awardedBadgesPS = runBadgeAchvService.checkAndAwardRunBadges(runRecordPS);

        // 4. 레벨업 서비스를 호출하여 사용자의 레벨을 업데이트합니다.
        runLevelService.updateUserLevelIfNeeded(user);

        // 5. 최종적으로, 저장된 기록과 새로 획득한 뱃지 목록을 DTO로 감싸 컨트롤러에 반환합니다.
        return new RunRecordResponse.SaveDTO(runRecordPS, awardedBadgesPS);

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

}