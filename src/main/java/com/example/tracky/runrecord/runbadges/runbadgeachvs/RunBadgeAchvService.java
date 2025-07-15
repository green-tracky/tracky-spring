package com.example.tracky.runrecord.runbadges.runbadgeachvs;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.runrecord.runbadges.RunBadge;
import com.example.tracky.runrecord.runbadges.RunBadgeRepository;
import com.example.tracky.runrecord.runbadges.runbadgeachvs.handler.RunBadgeAchvHandler;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 뱃지 획득 로직을 총괄하는 서비스 클래스입니다.
 * 사용자의 달리기 기록이 끝난 후 호출되어, 뱃지 타입에 따라 획득 조건을 검사하고
 * 적절한 핸들러(`RunBadgeAchvHandler`)를 호출하여 뱃지를 부여하는 '교통정리' 역할을 수행합니다.
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RunBadgeAchvService {

    // 각 엔티티의 DB 접근을 위한 Repository 및 로직 처리를 위한 Handler 주입
    private final RunBadgeRepository runBadgeRepository;
    private final RunRecordRepository runRecordRepository;
    private final RunBadgeAchvRepository runBadgeAchvRepository;
    private final RunBadgeAchvHandler runBadgeAchvHandler;

    /**
     * <pre>
     * 사용자의 달리기 기록을 기반으로 획득 가능한 모든 뱃지를 검사하고 부여합니다.
     * 이 메서드는 트랜잭션 내에서 실행되어 뱃지 획득 과정의 데이터 일관성을 보장합니다.
     * </pre>
     *
     * @param runRecordPS 새로 생성된 사용자의 달리기 기록
     * @return 이번 달리기를 통해 새로 획득한 뱃지 내역(RunBadgeAchv) 리스트
     */
    @Transactional
    public List<RunBadgeAchv> checkAndAwardRunBadges(RunRecord runRecordPS) {
        List<RunBadgeAchv> savedRunBadgeAchvs = new ArrayList<>(); // 새로 획득한 뱃지를 담을 리스트
        User user = runRecordPS.getUser();
        YearMonth currentMonth = YearMonth.from(runRecordPS.getCreatedAt()); // 러닝 등록 하는 월

        // 1. DB에서 모든 뱃지 정보와 사용자의 월간 기록을 미리 조회하여 성능 최적화?
        List<RunBadge> allRunBadgesPS = runBadgeRepository.findAll();
        Integer monthlyTotalDistance = runRecordRepository.findTotalDistanceByUserIdAndYearMonth(user.getId(), currentMonth);
        Integer monthlyRunCount = runRecordRepository.countByUserIdAndYearMonth(user.getId(), currentMonth);

        // 2. 모든 뱃지를 순회하며 획득 조건 검사
        for (RunBadge runBadge : allRunBadgesPS) {
            // 뱃지 타입별로 분기 처리
            switch (runBadge.getType()) {
                case RECORD:
                    // 최고 기록 뱃지 조건 검사
                    if (checkRecordBadgeCondition(runRecordPS, runBadge)) {
                        // 조건 만족 시 핸들러 호출
                        runBadgeAchvHandler.handleRecordBadge(runRecordPS, user, runBadge)
                                .ifPresent(runBadgeAchv -> savedRunBadgeAchvs.add(runBadgeAchv));
                    }
                    break;

                case MONTHLY_ACHIEVEMENT:
                    // 월간 업적 뱃지 조건 검사
                    if (checkMonthlyAchievementCondition(runRecordPS, runBadge, monthlyRunCount, monthlyTotalDistance, currentMonth, user)) {
                        runBadgeAchvHandler.handleMonthlyAchievement(runRecordPS, user, runBadge)
                                .ifPresent(runBadgeAchv -> savedRunBadgeAchvs.add(runBadgeAchv));
                    }
                    break;

                default:
                    // 기타 타입이 있다면 여기에 추가
                    break;
            }
        }

        log.info("{}({})이 {}를 획득했습니다.", user.getUsername(), user.getId(), allRunBadgesPS);

        return savedRunBadgeAchvs;
    }

    /**
     * 최고 기록 뱃지 조건 검사 메서드
     *
     * @param runRecordPS 현재 러닝 기록
     * @param runBadge    검사할 뱃지
     * @return 조건 만족 여부
     */
    private boolean checkRecordBadgeCondition(RunRecord runRecordPS, RunBadge runBadge) {
        switch (runBadge.getName()) {
            case "1K 최고 기록":
                // 1km 이상 5km 미만 달린 기록만 해당
                return runRecordPS.getTotalDistanceMeters() >= 1000 && runRecordPS.getTotalDistanceMeters() < 5000;
            case "5K 최고 기록":
                // 5km 이상 달린 기록만 해당
                return runRecordPS.getTotalDistanceMeters() >= 5000;
            default:
                return false;
        }
    }

    /**
     * 월간 업적 뱃지 조건 검사 메서드
     *
     * @param runRecordPS          현재 러닝 기록
     * @param runBadge             검사할 뱃지
     * @param monthlyRunCount      이번 달 러닝 횟수
     * @param monthlyTotalDistance 이번 달 누적 거리
     * @param currentMonth         현재 연월
     * @param user                 사용자
     * @return 조건 만족 여부
     */
    private boolean checkMonthlyAchievementCondition(RunRecord runRecordPS, RunBadge runBadge,
                                                     int monthlyRunCount, int monthlyTotalDistance,
                                                     YearMonth currentMonth, User user) {
        boolean isExists = runBadgeAchvRepository.existsByUserIdAndRunBadgeIdAndYearMonth(user.getId(), runBadge.getId(), currentMonth);

        switch (runBadge.getName()) {
            case "첫 시작":
                // 이번 달 첫 러닝인 경우
                return monthlyRunCount == 1;
            case "브론즈":
                // 누적 거리 24km 이상
                return monthlyTotalDistance >= 24000 && !isExists;
            case "실버":
                // 누적 거리 40km 이상
                return monthlyTotalDistance >= 40000 && !isExists;
            case "골드":
                // 누적 거리 80km 이상
                return monthlyTotalDistance >= 80000 && !isExists;
            case "플래티넘":
                // 누적 거리 160km 이상
                return monthlyTotalDistance >= 160000 && !isExists;
            default:
                return false;
        }

    }

}
