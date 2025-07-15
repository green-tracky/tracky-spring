package com.example.tracky.runrecord;

import com.example.tracky._core.constants.Constants;
import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky._core.values.TimeValue;
import com.example.tracky.community.challenges.ChallengeRewardService;
import com.example.tracky.community.challenges.domain.UserChallengeReward;
import com.example.tracky.community.challenges.repository.UserChallengeRewardRepository;
import com.example.tracky.runrecord.dto.*;
import com.example.tracky.runrecord.pictures.Picture;
import com.example.tracky.runrecord.runbadges.runbadgeachvs.RunBadgeAchv;
import com.example.tracky.runrecord.runbadges.runbadgeachvs.RunBadgeAchvRepository;
import com.example.tracky.runrecord.runbadges.runbadgeachvs.RunBadgeAchvService;
import com.example.tracky.runrecord.utils.RunRecordUtil;
import com.example.tracky.s3.S3Service;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.runlevel.RunLevel;
import com.example.tracky.user.runlevel.RunLevelRepository;
import com.example.tracky.user.runlevel.RunLevelService;
import com.example.tracky.user.runlevel.utils.RunLevelUtil;
import com.example.tracky.user.utils.LoginIdUtil;
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
    private final UserChallengeRewardRepository userChallengeRewardRepository;
    private final ChallengeRewardService challengeRewardService;
    private final S3Service s3Service;

    /**
     * 러닝 상세조회
     *
     * @param sessionProfile
     * @param id
     * @return
     */
    public RunRecordResponse.DetailDTO getRunRecord(OAuthProfile sessionProfile, Integer id) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 1. 러닝 기록 조회
        RunRecord runRecordPS = runRecordRepository.findByIdJoin(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

        // 2. 권한 체크
        checkAccess(userPS, runRecordPS);

        log.info("{}({})이 러닝{}을 상세 조회합니다.", userPS.getUsername(), userPS.getId(), runRecordPS.getId());

        // 3. 러닝 응답 DTO 로 변환
        return new RunRecordResponse.DetailDTO(runRecordPS);
    }

    /**
     * 주간 러닝 활동 통계를 조회
     * <p>
     * - 기준일을 포함한 주(월~일) 단위로 거리, 시간, 획득 배지, 최근 기록 리스트 반환
     * <p>
     *
     * @param sessionProfile 현재 사용자 정보
     * @param baseDate       기준 날짜
     * @param before         기준일로부터 몇 주 전을 조회할 것인지 (0 = 이번 주, 1 = 저번 주 등)
     * @return WeekDTO - 누적 통계(AvgStatsDTO), 배지 목록, 최근 러닝 기록 목록 포함
     */
    public RunRecordResponse.WeekDTO getActivitiesWeek(OAuthProfile sessionProfile, LocalDate baseDate, Integer before) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 1. 기준 주 계산
        LocalDate targetDate = baseDate.minusWeeks(before);
        LocalDate start = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = start.plusDays(6);
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        // 2. 주간 기록 조회
        List<RunRecord> runRecordListPS = runRecordsRepository.findAllByCreatedAtBetween(userPS.getId(), startTime, endTime);
        Integer totalDistanceMeters = 0;
        Integer totalDurationSeconds = 0;
        for (RunRecord record : runRecordListPS) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
        }

        // 3. 거리/시간 합산 및 DTO 생성
        AvgStatsDTO avgStats = RunRecordUtil.avgStats(runRecordListPS, totalDistanceMeters, totalDurationSeconds);

        // 4. 뱃지 조회
        List<RunBadgeAchv> runBadgesPS = runBadgeAchvRepository.findByUserIdJoin(userPS.getId());
        List<AchievementHistoryItemDTO> runBadgeList = runBadgesPS.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getRunBadge().getId(), // ID 기준으로 그룹핑
                        () -> new LinkedHashMap<>(),
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    RunBadgeAchv latest = entry.getValue().stream()
                            .max((a, b) -> a.getAchievedAt().compareTo(b.getAchievedAt()))
                            .orElse(entry.getValue().get(0));
                    Integer count = entry.getValue().size();
                    return new AchievementHistoryItemDTO(latest, count);
                })
                .collect(Collectors.toList());

        //  챌린지 보상 조회
        List<UserChallengeReward> challengeRewardsPS = userChallengeRewardRepository.findAllByUserId(userPS.getId());
        List<AchievementHistoryItemDTO> rewardList = challengeRewardsPS.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getRewardMaster() != null ? r.getRewardMaster().getId() : r.getRewardMaster().getRewardName(), // 사설 챌린지도 고려
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    UserChallengeReward latest = entry.getValue().stream()
                            .max((a, b) -> a.getReceivedAt().compareTo(b.getReceivedAt()))
                            .orElse(entry.getValue().get(0));
                    Integer count = entry.getValue().size();
                    return new AchievementHistoryItemDTO(latest, count);
                })
                .collect(Collectors.toList());


        // 통합 DTO로 변환
        List<AchievementHistoryItemDTO> achievementHistorys = new ArrayList<>();
        achievementHistorys.addAll(runBadgeList);
        achievementHistorys.addAll(rewardList);

        // 시간순으로 정렬
        achievementHistorys = achievementHistorys.stream()
                .sorted((a, b) -> {
                    LocalDateTime aTime = a.getAchievedAt();
                    LocalDateTime bTime = b.getAchievedAt();

                    // nullsLast 처리를 수동으로 구현
                    if (aTime == null && bTime == null) return 0;
                    if (aTime == null) return 1;
                    if (bTime == null) return -1;

                    return bTime.compareTo(aTime); // 내림차순 정렬 (reversed)
                })
                .limit(5) // 상위 5개만 선택
                .collect(Collectors.toList());


        // 5. 최근 3개 러닝 기록 + DTO 변환
        List<RunRecord> recentRunRecordsPS = runRecordsRepository.findTop3ByUserIdOrderByCreatedAtJoinBadgeAchv(userPS.getId());
        List<RecentRunsDTO> recentRunList = recentRunRecordsPS.stream()
                .map(r -> new RecentRunsDTO(r))
                .toList();

        // 6. 주차 라벨 생성 (기준 baseDate가 속한 '년-월'에 해당하는 주차만 필터링)
        String baseYearMonth = String.format("%04d-%02d", baseDate.getYear(), baseDate.getMonthValue());
        Map<String, Set<String>> weeksMap = new HashMap<>();

        for (RunRecord record : runRecordsRepository.findAllByUserId(userPS.getId())) {
            LocalDate date = record.getCreatedAt().toLocalDate();
            LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = startOfWeek.plusDays(6);

            // 주차 라벨: "MM.DD~MM.DD"
            String weekLabel = String.format("%02d.%02d~%02d.%02d",
                    startOfWeek.getMonthValue(), startOfWeek.getDayOfMonth(),
                    endOfWeek.getMonthValue(), endOfWeek.getDayOfMonth());

            // 주 시작일 월-년 문자열
            String startYearMonth = String.format("%04d-%02d", startOfWeek.getYear(), startOfWeek.getMonthValue());
            // 주 종료일 월-년 문자열
            String endYearMonth = String.format("%04d-%02d", endOfWeek.getYear(), endOfWeek.getMonthValue());

            // 기준 월과 주 시작일/종료일 중 하나라도 일치하면 포함
            if (baseYearMonth.equals(startYearMonth) || baseYearMonth.equals(endYearMonth)) {
                weeksMap.computeIfAbsent(baseYearMonth, k -> new HashSet<>()).add(weekLabel);
            }
        }

        Map<String, List<String>> weeksMapList = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : weeksMap.entrySet()) {
            List<String> sortedList = entry.getValue().stream()
                    .sorted(Comparator.comparing(label -> {
                        String[] parts = label.split("~")[0].split("\\.");
                        return LocalDate.of(Integer.parseInt(entry.getKey().split("-")[0]),
                                Integer.parseInt(parts[0]),
                                Integer.parseInt(parts[1]));
                    }))
                    .toList();
            weeksMapList.put(entry.getKey(), sortedList);
        }

        // 7. 레벨 정보 조회
        RunLevel currentLevelPS = userRepository.findByIdJoin(userPS.getId()).orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND)).getRunLevel();
        List<RunLevel> runLevelsPS = runLevelRepository.findAllByOrderBySortOrderAsc();
        Integer totalDistance = runRecordRepository.findTotalDistanceByUserId(userPS.getId());
        Integer distanceToNextLevel = RunLevelUtil.getDistanceToNextLevel(currentLevelPS, runLevelsPS, totalDistance);
        RunLevelDTO runLevel = new RunLevelDTO(currentLevelPS, totalDistance, distanceToNextLevel);

        // 8. DTO 반환
        RunRecordResponse.WeekDTO weekDTO = new RunRecordResponse.WeekDTO(avgStats, achievementHistorys, recentRunList, runLevel);
        weekDTO.setWeeks(weeksMapList);

        log.info("{}({})이 주간 통계를 조회합니다.", userPS.getUsername(), userPS.getId());

        return weekDTO;
    }

    /**
     * 월간 러닝 활동 통계를 조회
     * <p>
     * - 특정 연/월 내 기록된 러닝 정보를 기반으로 누적 통계, 배지, 최근 기록 리스트 반환
     * <p>
     *
     * @param sessionProfile 현재 사용자 정보
     * @param month          조회할 월 (1~12)
     * @param year           조회할 연도
     * @return MonthDTO - 누적 통계(AvgStatsDTO), 배지 목록, 최근 러닝 기록 목록 포함
     */
    public RunRecordResponse.MonthDTO getActivitiesMonth(OAuthProfile sessionProfile, Integer month, Integer year) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 1. 해당 월의 시작/끝 날짜 및 시간 계산
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        // 2. 해당 기간의 기록 조회 및 거리/시간 합산
        List<RunRecord> runRecordListPS = runRecordsRepository.findAllByCreatedAtBetween(userPS.getId(), startTime, endTime);
        int totalDistanceMeters = 0;
        int totalDurationSeconds = 0;
        for (RunRecord record : runRecordListPS) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
        }

        // 3. 누적 통계 DTO 생성
        AvgStatsDTO avgStats = RunRecordUtil.avgStats(runRecordListPS, totalDistanceMeters, totalDurationSeconds);

        // 4. 뱃지 조회
        List<RunBadgeAchv> runBadgesPS = runBadgeAchvRepository.findByUserIdJoin(userPS.getId());
        List<AchievementHistoryItemDTO> runBadgeList = runBadgesPS.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getRunBadge().getId(), // ID 기준으로 그룹핑
                        () -> new LinkedHashMap<>(),
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    RunBadgeAchv latest = entry.getValue().stream()
                            .max((a, b) -> a.getAchievedAt().compareTo(b.getAchievedAt()))
                            .orElse(entry.getValue().get(0));
                    Integer count = entry.getValue().size();
                    return new AchievementHistoryItemDTO(latest, count);
                })
                .collect(Collectors.toList());

        //  메달 조회
        List<UserChallengeReward> medals = userChallengeRewardRepository.findAllByUserId(userPS.getId());
        List<AchievementHistoryItemDTO> medalList = medals.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getRewardMaster() != null ? m.getRewardMaster().getId() : m.getRewardMaster().getRewardName(), // 사설 챌린지도 고려
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    UserChallengeReward latest = entry.getValue().stream()
                            .max((a, b) -> a.getReceivedAt().compareTo(b.getReceivedAt()))
                            .orElse(entry.getValue().get(0));
                    Integer count = entry.getValue().size();
                    return new AchievementHistoryItemDTO(latest, count);
                })
                .collect(Collectors.toList());


        // 통합 DTO로 변환
        List<AchievementHistoryItemDTO> achievementHistorys = new ArrayList<>();
        achievementHistorys.addAll(runBadgeList);
        achievementHistorys.addAll(medalList);

        // 시간순으로 정렬
        achievementHistorys = achievementHistorys.stream()
                .sorted((a, b) -> {
                    LocalDateTime aTime = a.getAchievedAt();
                    LocalDateTime bTime = b.getAchievedAt();

                    // nullsLast 처리를 수동으로 구현
                    if (aTime == null && bTime == null) return 0;
                    if (aTime == null) return 1;
                    if (bTime == null) return -1;

                    return bTime.compareTo(aTime); // 내림차순 정렬 (reversed)
                })
                .limit(5) // 상위 5개만 선택
                .collect(Collectors.toList());

        // 5. 최근 러닝 3개 변환
        List<RunRecord> recentRunRecords = runRecordsRepository.findTop3ByUserIdOrderByCreatedAtJoinBadgeAchv(userPS.getId());
        List<RecentRunsDTO> recentRunList = recentRunRecords.stream()
                .map(r -> new RecentRunsDTO(r))
                .limit(3)
                .toList();

        // 6. 기록이 있는 월/연도 목록 구성
        List<RunRecord> runRecordAll = runRecordsRepository.findAllByUserId(userPS.getId());
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
        RunLevel currentLevelPS = userRepository.findByIdJoin(userPS.getId())
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND))
                .getRunLevel();
        List<RunLevel> runLevelsPS = runLevelRepository.findAllByOrderBySortOrderAsc();
        Integer totalDistance = runRecordRepository.findTotalDistanceByUserId(userPS.getId());
        Integer distanceToNextLevel = RunLevelUtil.getDistanceToNextLevel(currentLevelPS, runLevelsPS, totalDistance);
        RunLevelDTO RunLevel = new RunLevelDTO(currentLevelPS, totalDistance, distanceToNextLevel);

        // 8. 최종 DTO 구성
        RunRecordResponse.MonthDTO monthDTO = new RunRecordResponse.MonthDTO(avgStats, achievementHistorys, recentRunList, RunLevel);
        monthDTO.setYears(yearSet.stream().sorted().toList());

        Map<Integer, List<Integer>> sortedMonthMap = new HashMap<>();
        for (Integer y : monthsMap.keySet()) {
            sortedMonthMap.put(y, monthsMap.get(y).stream().sorted().toList());
        }
        monthDTO.setMounts(sortedMonthMap);

        log.info("{}({})이 월간 통계를 조회합니다.", userPS.getUsername(), userPS.getId());

        return monthDTO;
    }

    /**
     * 연간 러닝 활동 통계를 조회
     * <p>
     * - 전체 거리/시간 기반 누적 통계 + 주간 평균 활동(평균 러닝 수, 거리 등) 반환
     * <p>
     *
     * @param sessionProfile 현재 사용자 정보
     * @param year           조회할 연도
     * @return YearDTO - 누적 통계(AvgStatsDTO), 평균 통계(TotalStatsDTO), 배지 목록, 최근 기록 목록 포함
     */
    public RunRecordResponse.YearDTO getActivitiesYear(OAuthProfile sessionProfile, Integer year) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 1. 연도 시작/끝 날짜 계산
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        // 2. 해당 연도의 기록 조회 및 총 거리/시간 계산
        List<RunRecord> runRecordList = runRecordsRepository.findAllByCreatedAtBetween(userPS.getId(), startTime, endTime);
        int totalDistanceMeters = 0;
        int totalDurationSeconds = 0;
        for (RunRecord record : runRecordList) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
        }


        // 3. 누적 통계용 AvgStatsDTO 생성
        AvgStatsDTO avgStats = RunRecordUtil.avgStats(runRecordList, totalDistanceMeters, totalDurationSeconds);

        // 4. 뱃지 조회
        List<RunBadgeAchv> runBadges = runBadgeAchvRepository.findByUserIdJoin(userPS.getId());
        List<AchievementHistoryItemDTO> runBadgeList = runBadges.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getRunBadge().getId(), // ID 기준으로 그룹핑
                        () -> new LinkedHashMap<>(),
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    RunBadgeAchv latest = entry.getValue().stream()
                            .max((a, b) -> a.getAchievedAt().compareTo(b.getAchievedAt()))
                            .orElse(entry.getValue().get(0));
                    Integer count = entry.getValue().size();
                    return new AchievementHistoryItemDTO(latest, count);
                })
                .collect(Collectors.toList());

        //  메달 조회
        List<UserChallengeReward> medals = userChallengeRewardRepository.findAllByUserId(userPS.getId());
        List<AchievementHistoryItemDTO> medalList = medals.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getRewardMaster() != null ? m.getRewardMaster().getId() : m.getRewardMaster().getRewardName(), // 사설 챌린지도 고려
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    UserChallengeReward latest = entry.getValue().stream()
                            .max((a, b) -> a.getReceivedAt().compareTo(b.getReceivedAt()))
                            .orElse(entry.getValue().get(0));
                    Integer count = entry.getValue().size();
                    return new AchievementHistoryItemDTO(latest, count);
                })
                .collect(Collectors.toList());


        // 통합 DTO로 변환
        List<AchievementHistoryItemDTO> achievementHistorys = new ArrayList<>();
        achievementHistorys.addAll(runBadgeList);
        achievementHistorys.addAll(medalList);

        // 시간순으로 정렬
        achievementHistorys = achievementHistorys.stream()
                .sorted((a, b) -> {
                    LocalDateTime aTime = a.getAchievedAt();
                    LocalDateTime bTime = b.getAchievedAt();

                    // nullsLast 처리를 수동으로 구현
                    if (aTime == null && bTime == null) return 0;
                    if (aTime == null) return 1;
                    if (bTime == null) return -1;

                    return bTime.compareTo(aTime); // 내림차순 정렬 (reversed)
                })
                .limit(5) // 상위 5개만 선택
                .collect(Collectors.toList());

        // 5. 최근 3개의 러닝 기록 조회 및 DTO 변환
        List<RunRecord> recentRunRecords = runRecordsRepository.findTop3ByUserIdOrderByCreatedAtJoinBadgeAchv(userPS.getId());
        List<RecentRunsDTO> recentRunList = recentRunRecords.stream()
                .map(r -> new RecentRunsDTO(r))
                .limit(3)
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
        List<RunRecord> runRecordAll = runRecordsRepository.findAllByUserId(userPS.getId());
        Set<Integer> yearData = new HashSet<>();
        for (RunRecord record : runRecordAll) {
            yearData.add(record.getCreatedAt().getYear());
        }

        // 8. 현재 레벨 및 누적 거리, 다음 레벨까지 남은 거리 계산
        RunLevel currentLevelPS = userRepository.findByIdJoin(userPS.getId())
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND))
                .getRunLevel();
        List<RunLevel> runLevelsPS = runLevelRepository.findAllByOrderBySortOrderAsc();
        Integer totalDistance = runRecordRepository.findTotalDistanceByUserId(userPS.getId());
        Integer distanceToNextLevel = RunLevelUtil.getDistanceToNextLevel(currentLevelPS, runLevelsPS, totalDistance);
        RunLevelDTO RunLevel = new RunLevelDTO(currentLevelPS, totalDistance, distanceToNextLevel);

        // 9. 최종 DTO 생성 및 반환
        RunRecordResponse.YearDTO yearDTO = new RunRecordResponse.YearDTO(avgStats, allStats, achievementHistorys, recentRunList, RunLevel);
        yearDTO.setYears(new ArrayList<>(yearData));

        log.info("{}({})이 연간 통계를 조회합니다.", userPS.getUsername(), userPS.getId());

        return yearDTO;
    }

    /**
     * 전체 러닝 활동 통계를 조회
     * <p>
     * - 모든 기록을 바탕으로 누적 통계 + 주당 평균 활동 정보 반환
     * <p>
     *
     * @param sessionProfile 현재 사용자 정보
     * @return AllDTO - 누적 통계(AvgStatsDTO), 평균 통계(TotalStatsDTO), 배지 목록, 전체 기록 목록 포함
     */
    public RunRecordResponse.AllDTO getActivitiesAll(OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 1. 전체 러닝 기록 조회
        List<RunRecord> runRecords = runRecordsRepository.findAllByUserId(userPS.getId());

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

        // 4. 뱃지 조회
        List<RunBadgeAchv> runBadges = runBadgeAchvRepository.findByUserIdJoin(userPS.getId());
        List<AchievementHistoryItemDTO> runBadgeList = runBadges.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getRunBadge().getId(), // ID 기준으로 그룹핑
                        () -> new LinkedHashMap<>(),
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    RunBadgeAchv latest = entry.getValue().stream()
                            .max((a, b) -> a.getAchievedAt().compareTo(b.getAchievedAt()))
                            .orElse(entry.getValue().get(0));
                    Integer count = entry.getValue().size();
                    return new AchievementHistoryItemDTO(latest, count);
                })
                .collect(Collectors.toList());

        //  메달 조회
        List<UserChallengeReward> medals = userChallengeRewardRepository.findAllByUserId(userPS.getId());
        List<AchievementHistoryItemDTO> medalList = medals.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getRewardMaster() != null ? m.getRewardMaster().getId() : m.getRewardMaster().getRewardName(), // 사설 챌린지도 고려
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    UserChallengeReward latest = entry.getValue().stream()
                            .max((a, b) -> a.getReceivedAt().compareTo(b.getReceivedAt()))
                            .orElse(entry.getValue().get(0));
                    Integer count = entry.getValue().size();
                    return new AchievementHistoryItemDTO(latest, count);
                })
                .collect(Collectors.toList());


        // 통합 DTO로 변환
        List<AchievementHistoryItemDTO> achievementHistorys = new ArrayList<>();
        achievementHistorys.addAll(runBadgeList);
        achievementHistorys.addAll(medalList);

        // 시간순으로 정렬
        achievementHistorys = achievementHistorys.stream()
                .sorted((a, b) -> {
                    LocalDateTime aTime = a.getAchievedAt();
                    LocalDateTime bTime = b.getAchievedAt();

                    // nullsLast 처리를 수동으로 구현
                    if (aTime == null && bTime == null) return 0;
                    if (aTime == null) return 1;
                    if (bTime == null) return -1;

                    return bTime.compareTo(aTime); // 내림차순 정렬 (reversed)
                })
                .limit(5) // 상위 5개만 선택
                .collect(Collectors.toList());

        // 5. 최근 3개의 러닝 기록 조회
        List<RunRecord> recentRunRecords = runRecordsRepository.findTop3ByUserIdOrderByCreatedAtJoinBadgeAchv(userPS.getId());
        List<RecentRunsDTO> recentRunList = recentRunRecords.stream()
                .map(r -> new RecentRunsDTO(r))
                .limit(3)
                .toList();

        // 6. 현재 레벨, 총 거리, 다음 레벨까지 거리 계산
        RunLevel currentLevelPS = userRepository.findByIdJoin(userPS.getId())
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND))
                .getRunLevel();
        List<RunLevel> runLevelsPS = runLevelRepository.findAllByOrderBySortOrderAsc();
        Integer totalDistance = runRecordRepository.findTotalDistanceByUserId(userPS.getId());
        Integer distanceToNextLevel = RunLevelUtil.getDistanceToNextLevel(currentLevelPS, runLevelsPS, totalDistance);
        RunLevelDTO RunLevel = new RunLevelDTO(currentLevelPS, totalDistance, distanceToNextLevel);

        // 7. 전체 주 수 계산 (최초 기록 ~ 마지막 기록 사이의 월~일 기준 주차 수)
        if (runRecords.isEmpty()) {
            TotalStatsDTO allStats = new TotalStatsDTO(0.0, 0, 0, 0);
            return new RunRecordResponse.AllDTO(stats, allStats, achievementHistorys, recentRunList, RunLevel);
        }

        LocalDateTime start = runRecords.stream().map(RunRecord::getCreatedAt).min(Comparator.naturalOrder()).orElse(TimeValue.getServerTime());
        LocalDateTime end = runRecords.stream().map(RunRecord::getCreatedAt).max(Comparator.naturalOrder()).orElse(TimeValue.getServerTime());
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

        log.info("{}({})이 전체통계를 조회합니다.", userPS.getUsername(), userPS.getId());

        // 9. 최종 DTO 반환
        return new RunRecordResponse.AllDTO(stats, allStats, achievementHistorys, recentRunList, RunLevel);
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
     * @param sessionProfile 통계를 조회할 사용자 정보
     * @param order          정렬 기준 (latest, oldest)
     * @param year           기준 연도
     * @return GroupedRecentListDTO - 연도/월별 러닝 통계 + 상세 기록 리스트 포함 DTO
     */
    public RunRecordResponse.GroupedRecentListDTO getGroupedActivities(OAuthProfile sessionProfile, String order, Integer year, Integer page) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        List<RunRecord> runRecords = runRecordsRepository.findAllByUserId(userPS.getId());
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
        int size = Constants.RUN_LIST_FETCH_SIZE;
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

        log.info("{}({})이 최근 러닝 목록을 {}기준으로 정렬합니다.", userPS.getUsername(), userPS.getId(), order);

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
     * @param sessionProfile 사용자 정보
     * @param order          정렬 기준 (distance-asc, distance-desc, pace-asc, pace-desc)
     * @param year           기준 연도
     * @return FlatRecentListDTO - 정렬된 러닝 기록 리스트 포함 DTO
     */
    public RunRecordResponse.FlatRecentListDTO getFlatActivities(OAuthProfile sessionProfile, String order, Integer year, Integer page) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        List<RunRecord> runRecords;
        switch (order) {
            case "distance-desc" ->
                    runRecords = runRecordsRepository.findAllByUserIdOrderByDistanceDesc(userPS.getId(), page);
            case "distance-asc" ->
                    runRecords = runRecordsRepository.findAllByUserIdOrderByDistanceAsc(userPS.getId(), page);
            case "pace-desc" ->
                    runRecords = runRecordsRepository.findAllByUserIdOrderByAvgPaceDesc(userPS.getId(), page);
            case "pace-asc" -> runRecords = runRecordsRepository.findAllByUserIdOrderByAvgPaceAsc(userPS.getId(), page);
            default ->
                    runRecords = runRecordsRepository.findAllByUserIdOrderByCreatedAtDesc(userPS.getId(), page); // 기본: 최신순
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
        Long totalcount = runRecordRepository.totalCount(userPS.getId());

        PageDTO pageing = new PageDTO(totalcount.intValue(), page);

        log.info("{}({})이 최근 러닝 목록을 {}기준으로 정렬합니다.", userPS.getUsername(), userPS.getId(), order);

        return new RunRecordResponse.FlatRecentListDTO(recentRuns, pageing);
    }


    /**
     * 러닝 저장
     *
     * @param sessionProfile
     * @param reqDTO
     */
    @Transactional
    public RunRecordResponse.SaveDTO save(OAuthProfile sessionProfile, RunRecordRequest.SaveDTO reqDTO) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 1. DTO를 엔티티로 변환합니다.
        RunRecord runRecord = reqDTO.toEntity(userPS);

        // 2. 달리기 기록 엔티티를 데이터베이스에 저장합니다.
        RunRecord runRecordPS = runRecordRepository.save(runRecord);

        // 3. 뱃지 서비스를 호출하여, 저장된 기록에 대해 획득 가능한 모든 뱃지를 확인하고 부여합니다.
        // 이 과정에서 새로 획득한 뱃지 목록을 반환받습니다.
        List<RunBadgeAchv> awardedBadgesPS = runBadgeAchvService.checkAndAwardRunBadges(runRecordPS);

        // 4. 레벨업 서비스를 호출하여 사용자의 레벨을 업데이트합니다.
        runLevelService.updateUserLevelIfNeeded(userPS);

        // 5. 러닝 저장시 챌린지 보상 획득(공개, 사설(완주자))
        List<UserChallengeReward> awardedChallengeRewardsPS = challengeRewardService.checkAndAwardChallengeRewards(userPS);

        log.info("{}({})이 러닝을 저장했습니다.", userPS.getUsername(), userPS.getId());

        // 6. 최종적으로, 저장된 기록과 새로 획득한 뱃지 목록을 DTO로 감싸 컨트롤러에 반환합니다.
        return new RunRecordResponse.SaveDTO(runRecordPS, awardedBadgesPS);

    }

    /**
     * 러닝 삭제
     *
     * @param sessionProfile
     * @param id             runRecordId
     * @return
     */
    @Transactional
    public void delete(OAuthProfile sessionProfile, Integer id) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 러닝 기록 조회
        RunRecord runRecordPS = runRecordRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

        // 권한 체크
        checkAccess(userPS, runRecordPS);

        // aws s3 파일 삭제
        for (Picture p : runRecordPS.getPictures()) {
            s3Service.deleteFileByUrl(p.getFileUrl());
        }

        // 삭제
        runRecordRepository.delete(runRecordPS);

        log.info("{}({})이 {}번 러닝을 삭제했습니다.", userPS.getUsername(), userPS.getId(), runRecordPS.getId());
    }

    @Transactional
    public RunRecordResponse.UpdateDTO update(OAuthProfile sessionProfile, Integer id, RunRecordRequest.UpdateDTO reqDTO) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 1. 러닝 기록 조회
        RunRecord runRecordPS = runRecordRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

        // 2. 권한 체크
        checkAccess(userPS, runRecordPS);

        // 3. 러닝 내용 수정
        runRecordPS.update(reqDTO);

        log.info("{}({})이 {}번 러닝을 수정했습니다.", userPS.getUsername(), userPS.getId(), runRecordPS.getId());

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
    private void checkAccess(User user, RunRecord runRecord) {
        if (!runRecord.getUser().getId().equals(user.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }
    }

    /**
     * 게시글 등록할때 사용하는 러닝 목록
     *
     * @param sessionProfile
     * @return
     */
    public List<RunRecordResponse.SimpleDTO> getRunRecords(OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 러닝 조회
        return runRecordRepository.findAllByUserId(userPS.getId()).stream()
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt())) // 최신순
                .map(runRecord -> new RunRecordResponse.SimpleDTO(runRecord))
                .toList();
    }
}