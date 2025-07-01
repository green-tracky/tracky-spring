package com.example.tracky.runrecord;

import com.example.tracky._core.error.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.runrecord.dto1.AvgStatsDTO;
import com.example.tracky.runrecord.dto1.RecentRunsDTO;
import com.example.tracky.runrecord.dto1.TotalStatsDTO;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchv;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvRepository;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvService;
import com.example.tracky.runrecord.utils.RunRecordUtil;
import com.example.tracky.user.User;
import com.example.tracky.user.runlevel.RunLevelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunRecordService {

    private final RunRecordRepository runRecordRepository;
    private final RunBadgeAchvService runBadgeAchvService;
    private final RunLevelService runLevelService;
    private final RunRecordRepository runRecordsRepository;
    private final RunBadgeAchvRepository runBadgeAchvRepository;

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
    public RunRecordResponse.WeekDTO getActivitiesWeek(LocalDate baseDate, User user) {
        // 📌 1. 기준 주(월~일)의 시작/끝 날짜 계산
        LocalDate start = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = start.plusDays(6);
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        // 📌 2. 해당 주간의 기록 조회 및 총 거리/시간 계산
        List<RunRecord> runRecordList = runRecordsRepository.findAllByCreatedAtBetween(user.getId(), startTime, endTime);
        Integer totalDistanceMeters = 0;
        Integer totalDurationSeconds = 0;
        for (RunRecord record : runRecordList) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
        }

        // 📌 3. 누적 통계용 AvgStatsDTO 생성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();
        int statsCount = runRecordList.size();
        Integer avgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);
        AvgStatsDTO stats = new AvgStatsDTO(runRecord, statsCount, avgPace);

        // 📌 4. 배지 리스트 생성
        List<RunBadgeAchv> runBadges = runBadgeAchvRepository.findByUserIdJoin(user.getId());
        List<RunBadgeResponse.DTO> runBadgeList = new ArrayList<>();
        for (RunBadgeAchv badge : runBadges) {
            runBadgeList.add(new RunBadgeResponse.DTO(badge));
        }

        // 📌 5. 최근 3개 러닝 기록 + DTO 변환
        List<RunRecord> recentRunRecords = runRecordsRepository.findTop3ByUserIdOrderByCreatedAtJoinBadgeAchv(user.getId());
        List<RecentRunsDTO> recentRunList = recentRunRecords.stream()
                .map(RecentRunsDTO::new)
                .toList();


        // 📌 6. 주차 목록 구성 (기록이 있는 주차)
        List<RunRecord> runRecordAll = runRecordsRepository.findAllByUserId(user.getId());
        Map<String, Set<String>> weeksMap = new HashMap<>();
        for (RunRecord record : runRecordAll) {
            LocalDate date = record.getCreatedAt().toLocalDate();
            int year = date.getYear();
            int month = date.getMonthValue();

            LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = startOfWeek.plusDays(6);

            String weekLabel = startOfWeek.getMonthValue() + "." + startOfWeek.getDayOfMonth()
                    + "~" + endOfWeek.getMonthValue() + "." + endOfWeek.getDayOfMonth();

            String key = year + "-" + String.format("%02d", month);
            weeksMap.computeIfAbsent(key, k -> new HashSet<>()).add(weekLabel);
        }

        // 📌 7. 현재 월 기준으로 주차 목록 정렬 및 DTO에 세팅
        String baseYearMonth = baseDate.getYear() + "-" + String.format("%02d", baseDate.getMonthValue());
        Map<String, List<String>> sortedWeeksMap = new HashMap<>();
        if (weeksMap.containsKey(baseYearMonth)) {
            List<String> sortedWeeks = weeksMap.get(baseYearMonth).stream()
                    .sorted(Comparator.comparing(label -> {
                        // 주 시작 날짜 추출
                        String[] startEnd = label.split("~")[0].split("\\.");
                        int monthPart = Integer.parseInt(startEnd[0]);
                        int dayPart = Integer.parseInt(startEnd[1]);
                        return LocalDate.of(baseDate.getYear(), monthPart, dayPart);
                    }))
                    .map(label -> {
                        // 오늘 날짜 기준으로 이번주/저번주 구하기
                        LocalDate today = LocalDate.now();

                        LocalDate thisWeekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                        LocalDate lastWeekStart = thisWeekStart.minusWeeks(1);

                        // 라벨에서 시작일 파싱
                        String[] startParts = label.split("~")[0].split("\\.");
                        int month = Integer.parseInt(startParts[0]);
                        int day = Integer.parseInt(startParts[1]);
                        LocalDate weekStart = LocalDate.of(baseDate.getYear(), month, day);

                        if (weekStart.equals(thisWeekStart)) {
                            return "이번주";
                        } else if (weekStart.equals(lastWeekStart)) {
                            return "저번주";
                        } else {
                            return label;
                        }
                    })
                    .toList();

            sortedWeeksMap.put(baseYearMonth, sortedWeeks);
        }

        // 📌 8. 최종 DTO 반환
        RunRecordResponse.WeekDTO weekDTO = new RunRecordResponse.WeekDTO(stats, runBadgeList, recentRunList);
        weekDTO.setWeeks(sortedWeeksMap);
        return weekDTO;
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
    public RunRecordResponse.MonthDTO getActivitiesMonth(Integer month, Integer year, User user) {
        // 📌 1. 월 시작/끝 날짜 계산
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        // 📌 2. 해당 월의 기록 조회 및 총 거리/시간 계산
        List<RunRecord> runRecordList = runRecordsRepository.findAllByCreatedAtBetween(user.getId(), startTime, endTime);
        Integer totalDistanceMeters = 0;
        Integer totalDurationSeconds = 0;
        for (RunRecord record : runRecordList) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
        }

        // 📌 3. 누적 통계용 AvgStatsDTO 생성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();
        int statsCount = runRecordList.size();
        Integer avgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);
        AvgStatsDTO stats = new AvgStatsDTO(runRecord, statsCount, avgPace);

        // 📌 4. 배지 리스트 생성
        List<RunBadgeAchv> runBadges = runBadgeAchvRepository.findByUserIdJoin(user.getId());
        List<RunBadgeResponse.DTO> runBadgeList = new ArrayList<>();
        for (RunBadgeAchv badge : runBadges) {
            runBadgeList.add(new RunBadgeResponse.DTO(badge));
        }

        // 📌 5. 최근 3개 러닝 기록 + DTO 변환
        List<RunRecord> recentRunRecords = runRecordsRepository.findTop3ByUserIdOrderByCreatedAtJoinBadgeAchv(user.getId());
        List<RecentRunsDTO> recentRunList = recentRunRecords.stream()
                .map(RecentRunsDTO::new)
                .toList();

        // 📌 6. 연도별-월 리스트 구성 (기록 기준)
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

        // 📌 7. 최종 DTO 생성 및 연도/월 정보 세팅
        RunRecordResponse.MonthDTO monthDTO = new RunRecordResponse.MonthDTO(stats, runBadgeList, recentRunList);
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
     * @param year 조회할 연도
     * @return YearDTO - 누적 통계(AvgStatsDTO), 평균 통계(TotalStatsDTO), 배지 목록, 최근 기록 목록 포함
     */
    public RunRecordResponse.YearDTO getActivitiesYear(Integer year, User user) {
        // 📌 1. 연도 시작/끝 날짜 계산
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        // 📌 2. 해당 연도의 기록 조회 및 총 거리/시간 계산
        List<RunRecord> runRecordList = runRecordsRepository.findAllByCreatedAtBetween(user.getId(), startTime, endTime);
        Integer totalDistanceMeters = 0;
        Integer totalDurationSeconds = 0;
        for (RunRecord record : runRecordList) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
        }

        // 📌 3. 누적 통계용 AvgStatsDTO 생성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();
        int statsCount = runRecordList.size();
        Integer avgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);
        AvgStatsDTO stats = new AvgStatsDTO(runRecord, statsCount, avgPace);

        // 📌 4. 배지 리스트 생성
        List<RunBadgeAchv> runBadges = runBadgeAchvRepository.findByUserIdJoin(user.getId());
        List<RunBadgeResponse.DTO> runBadgeList = new ArrayList<>();
        for (RunBadgeAchv badge : runBadges) {
            runBadgeList.add(new RunBadgeResponse.DTO(badge));
        }

        // 📌 5. 최근 3개 러닝 기록 + DTO 변환
        List<RunRecord> recentRunRecords = runRecordsRepository.findTop3ByUserIdOrderByCreatedAtJoinBadgeAchv(user.getId());
        List<RecentRunsDTO> recentRunList = recentRunRecords.stream()
                .map(RecentRunsDTO::new)
                .toList();

        // 📌 6. 주간 평균 통계 계산
        long totalWeeksInYear = ChronoUnit.WEEKS.between(
                start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                end.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        ) + 1;
        double avgCountData = statsCount > 0 ? (double) statsCount / totalWeeksInYear : 0;
        double avgCount = Math.floor(avgCountData * 10) / 10.0;
        Integer avgDistanceMeters = statsCount > 0 ? totalDistanceMeters / statsCount : 0;
        Integer avgDurationSeconds = statsCount > 0 ? totalDurationSeconds / statsCount : 0;
        Integer statsAvgPace = RunRecordUtil.calculatePace(avgDistanceMeters, avgDurationSeconds);
        TotalStatsDTO allStats = new TotalStatsDTO(avgCount, statsAvgPace, avgDistanceMeters, avgDurationSeconds);

        // 📌 7. 기록이 있는 연도 목록 추출
        List<RunRecord> runRecordAll = runRecordsRepository.findAllByUserId(user.getId());
        Set<Integer> yearData = new HashSet<>();
        for (RunRecord record : runRecordAll) {
            yearData.add(record.getCreatedAt().getYear());
        }

        // 📌 8. 최종 DTO 반환
        RunRecordResponse.YearDTO yearDTO = new RunRecordResponse.YearDTO(stats, allStats, runBadgeList, recentRunList);
        yearDTO.setYears(new ArrayList<>(yearData));

        return yearDTO;
    }

    /**
     * 전체 러닝 활동 통계를 조회
     * <p>
     * - 모든 기록을 바탕으로 누적 통계 + 주당 평균 활동 정보 반환
     * <p>
     *
     * @return AllDTO - 누적 통계(AvgStatsDTO), 평균 통계(TotalStatsDTO), 배지 목록, 전체 기록 목록 포함
     */
    public RunRecordResponse.AllDTO getActivitiesAll(User user) {
        // 📌 1. 전체 기록 조회
        List<RunRecord> runRecords = runRecordsRepository.findAllByUserId(user.getId());

        // 📌 2. 총 거리/시간 계산
        Integer totalDistanceMeters = 0;
        Integer totalDurationSeconds = 0;
        for (RunRecord record : runRecords) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
        }

        // 📌 3. 누적 통계용 AvgStatsDTO 생성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();
        int statsCount = runRecords.size();
        Integer avgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);
        AvgStatsDTO stats = new AvgStatsDTO(runRecord, statsCount, avgPace);

        // 📌 4. 배지 리스트 생성
        List<RunBadgeAchv> runBadges = runBadgeAchvRepository.findByUserIdJoin(user.getId());
        List<RunBadgeResponse.DTO> runBadgeList = new ArrayList<>();
        for (RunBadgeAchv badge : runBadges) {
            runBadgeList.add(new RunBadgeResponse.DTO(badge));
        }

        // 📌 5. 최근 3개 러닝 기록 + DTO 변환
        List<RunRecord> recentRunRecords = runRecordsRepository.findTop3ByUserIdOrderByCreatedAtJoinBadgeAchv(user.getId());
        List<RecentRunsDTO> recentRunList = recentRunRecords.stream()
                .map(RecentRunsDTO::new)
                .toList();

        // 📌 6. 전체 주 수 계산 (월~일 단위로 포함)
        if (runRecords.isEmpty()) {
            TotalStatsDTO allStats = new TotalStatsDTO(0.0, 0, 0, 0);
            return new RunRecordResponse.AllDTO(stats, allStats, runBadgeList, recentRunList);
        }

        LocalDateTime start = runRecords.stream().map(RunRecord::getCreatedAt).min(Comparator.naturalOrder()).orElse(LocalDateTime.now());
        LocalDateTime end = runRecords.stream().map(RunRecord::getCreatedAt).max(Comparator.naturalOrder()).orElse(LocalDateTime.now());
        LocalDateTime adjustedStart = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime adjustedEnd = end.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        long weeks = ChronoUnit.WEEKS.between(adjustedStart, adjustedEnd) + 1;

        double avgCountData = statsCount > 0 ? (double) statsCount / weeks : 0;
        double avgCount = Math.floor(avgCountData * 10) / 10.0;
        Integer avgDistanceMeters = statsCount > 0 ? totalDistanceMeters / statsCount : 0;
        Integer avgDurationSeconds = statsCount > 0 ? totalDurationSeconds / statsCount : 0;
        Integer statsAvgPace = RunRecordUtil.calculatePace(avgDistanceMeters, avgDurationSeconds);

        // 📌 7. 평균 통계 생성 + 최종 DTO 반환
        TotalStatsDTO allStats = new TotalStatsDTO(avgCount, statsAvgPace, avgDistanceMeters, avgDurationSeconds);
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