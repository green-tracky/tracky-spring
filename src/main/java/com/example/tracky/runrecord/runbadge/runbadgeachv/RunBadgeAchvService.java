package com.example.tracky.runrecord.runbadge.runbadgeachv;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.runrecord.runbadge.Enum.RunBadgeType;
import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.runrecord.runbadge.RunBadgeRepository;
import com.example.tracky.runrecord.runbadge.runbadgeachv.handler.RunBadgeAchvHandler;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * 뱃지 획득과 관련된 비즈니스 로직을 총괄하는 서비스 클래스입니다.
 * 이 서비스는 전체적인 흐름을 제어하고, 실제 DB 조작 및 세부 로직은 RunBadgeAchvHandler에 위임합니다.
 */
@Service
@RequiredArgsConstructor
public class RunBadgeAchvService {

    private final RunBadgeRepository runBadgeRepository;
    private final RunRecordRepository runRecordRepository;
    private final RunBadgeAchvHandler runBadgeAchvHandler;

    /**
     * <pre>
     * 새로운 달리기 기록에 대해 획득 가능한 모든 뱃지(단일/누적)를 확인하고 부여함
     * 단일 : 중복가능 여러 러닝이 가질 수 있음
     * 누적 : 하나의 러닝만 가질 수 있음. 갱신형
     * 이 메서드는 다른 서비스(RunRecordService)에서 호출되는 공개 진입점
     * </pre>
     *
     * @param runRecord 새로 저장된 달리기 기록
     * @return 이번 달리기를 통해 새로 획득하거나 갱신된 뱃지 목록
     */
    @Transactional
    public List<RunBadgeAchv> checkAndAwardBadges(RunRecord runRecord) {
        List<RunBadgeAchv> awarded = new ArrayList<>();

        // 1. '단일 기록'만으로 판단 가능한 뱃지들을 먼저 확인합니다.
        awarded.addAll(checkAndAwardSingleRunBadges(runRecord));

        // 2. '누적 기록'으로 판단 가능한 뱃지들을 확인합니다.
        // 이 뱃지들은 "이번 달리기로 인해" 달성되었으므로, 해당 달리기 기록(runRecord)에 연결됩니다.
        awarded.addAll(checkAndAwardCumulativeBadges(runRecord));

        return awarded;
    }

    /**
     * '단일 기록' 기반 뱃지 획득을 처리하는 내부 메서드입니다. ('첫 시작', '최고 기록')
     *
     * @param runRecord 검사할 달리기 기록
     * @return 이번 확인 과정에서 획득된 뱃지 목록
     */
    private List<RunBadgeAchv> checkAndAwardSingleRunBadges(RunRecord runRecord) {
        User user = runRecord.getUser();
        List<RunBadge> allBadges = runBadgeRepository.findAll();
        List<RunBadgeAchv> awardedInThisCheck = new ArrayList<>();

        for (RunBadge badge : allBadges) {
            // '첫 시작' 뱃지는 다른 뱃지들과 조건이 다르므로 특별 처리합니다.
            if ("첫 시작".equals(badge.getName())) {
                YearMonth currentMonth = YearMonth.from(runRecord.getCreatedAt().toLocalDateTime());
                // 이번 달에 이 유저의 러닝 기록이 정확히 1개일 때 (즉, 방금 저장된 이것뿐일 때) '첫 시작' 조건을 만족합니다.
                if (runRecordRepository.countByUserIdAndYearMonth(user.getId(), currentMonth) == 1) {
                    // '첫 시작'은 매달 획득 가능하므로, 일반적인 업적 핸들러를 사용합니다.
                    // (만약 월별 중복을 막고 싶다면 이 부분에 별도 로직이 필요하지만, 현재는 매번 새로 부여 시도)
                    runBadgeAchvHandler.handleAchievementBadge(runRecord, user, badge)
                            .ifPresent(awardedInThisCheck::add);
                }
            }

            // '기록' 타입의 뱃지들(1K, 5K 최고 기록)을 처리합니다.
            if (badge.getType() == RunBadgeType.RECORD) {
                // 이 달리기가 최고 기록 뱃지의 최소 조건(예: 5K 뱃지 -> 최소 5km 달리기)을 만족하는지 확인합니다.
                if (isConditionForRecordBadgeMet(runRecord, badge)) {
                    // 조건을 만족하면, 핸들러에 보내 기존 기록과 비교 및 갱신 처리를 위임합니다.
                    runBadgeAchvHandler.handleRecordBadge(runRecord, user, badge)
                            .ifPresent(awardedInThisCheck::add);
                }
            }
        }
        return awardedInThisCheck;
    }

    /**
     * '누적 기록' 기반 뱃지 획득을 처리하는 내부 메서드입니다. (브론즈 ~ 플래티넘)
     *
     * @param runRecord 뱃지 획득의 계기가 된 달리기 기록
     * @return 이번 확인 과정에서 획득된 뱃지 목록
     */
    private List<RunBadgeAchv> checkAndAwardCumulativeBadges(RunRecord runRecord) {
        User user = runRecord.getUser();
        List<RunBadge> allBadges = runBadgeRepository.findAll();
        List<RunBadgeAchv> awardedInThisCheck = new ArrayList<>();

        // 1. DB 조회를 최소화하기 위해, 현재 달리기 기록을 포함한 최신 누적 거리를 한 번만 조회합니다.
        Integer totalDistance = runRecordRepository.findTotalDistanceByUserId(user.getId());

        for (RunBadge badge : allBadges) {
            String badgeName = badge.getName();

            // 2. 누적 거리 뱃지 조건들을 순차적으로 확인합니다.
            // 핸들러의 'handleAchievementBadge'는 이미 획득한 뱃지는 다시 주지 않으므로, 여러 번 호출되어도 안전합니다.
            if ("브론즈".equals(badgeName) && totalDistance >= 10000) {
                runBadgeAchvHandler.handleAchievementBadge(runRecord, user, badge).ifPresent(awardedInThisCheck::add);
            } else if ("실버".equals(badgeName) && totalDistance >= 20000) {
                runBadgeAchvHandler.handleAchievementBadge(runRecord, user, badge).ifPresent(awardedInThisCheck::add);
            } else if ("골드".equals(badgeName) && totalDistance >= 40000) {
                runBadgeAchvHandler.handleAchievementBadge(runRecord, user, badge).ifPresent(awardedInThisCheck::add);
            } else if ("플래티넘".equals(badgeName) && totalDistance >= 80000) {
                runBadgeAchvHandler.handleAchievementBadge(runRecord, user, badge).ifPresent(awardedInThisCheck::add);
            }
        }
        return awardedInThisCheck;
    }

    /**
     * '최고 기록' 뱃지를 획득하기 위한 최소 조건을 만족하는지 확인하는 헬퍼 메서드입니다.
     *
     * @param runRecord 검사할 달리기 기록
     * @param runBadge  검사할 뱃지
     * @return 최소 조건을 만족하면 true
     */
    private boolean isConditionForRecordBadgeMet(RunRecord runRecord, RunBadge runBadge) {
        switch (runBadge.getName()) {
            case "1K 최고 기록":
                // 1km 최고 기록 뱃지를 받으려면 최소 1km는 달려야 합니다.
                return runRecord.getTotalDistanceMeters() >= 1000;
            case "5K 최고 기록":
                // 5km 최고 기록 뱃지를 받으려면 최소 5km는 달려야 합니다.
                return runRecord.getTotalDistanceMeters() >= 5000;
            default:
                // 정의되지 않은 기록 뱃지는 조건을 만족하지 않는 것으로 처리합니다.
                return false;
        }
    }
}
