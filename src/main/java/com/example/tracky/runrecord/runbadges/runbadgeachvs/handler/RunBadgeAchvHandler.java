package com.example.tracky.runrecord.runbadges.runbadgeachvs.handler;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runbadges.RunBadge;
import com.example.tracky.runrecord.runbadges.runbadgeachvs.RunBadgeAchv;
import com.example.tracky.runrecord.runbadges.runbadgeachvs.RunBadgeAchvRepository;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <pre>
 * 뱃지 획득(RunBadgeAchv)과 관련된 세부적인 생성, 삭제, 비교 로직을 처리하는 컴포넌트입니다.
 * 주로 사용자의 달리기 기록에 기반하여 뱃지 획득 조건을 평가하고, 뱃지를 부여하거나 갱신하는 역할을 수행합니다.
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class RunBadgeAchvHandler {

    // 뱃지 획득 내역 데이터베이스 접근을 위한 Repository 주입
    private final RunBadgeAchvRepository runBadgeAchvRepository;

    /**
     * <pre>
     * 최고 기록 뱃지(예: 1K, 5K 최고 기록)와 같이 기존 기록을 갱신하는 뱃지 유형에 대한 획득 로직을 처리합니다.
     * 새로운 달리기 기록이 기존 뱃지 기록보다 우수한 경우, 기존 뱃지 기록을 삭제하고 새로운 기록으로 뱃지를 갱신합니다.
     * </pre>
     *
     * @param newRunRecord 현재 달성된 새로운 달리기 기록
     * @param user         뱃지를 획득하려는 사용자
     * @param runBadge     평가할 뱃지 (예: "1K 최고 기록" 뱃지 객체)
     * @return 뱃지를 획득했거나 갱신했다면 새로 생성된 RunBadgeAchv를 담은 Optional, 그렇지 않다면 빈 Optional
     */
    public Optional<RunBadgeAchv> handleRecordBadge(RunRecord newRunRecord, User user, RunBadge runBadge) {
        // 1. 해당 사용자가 이미 이 뱃지를 가지고 있는지 확인
        Optional<RunBadgeAchv> existingRunBadgeAchvOP = runBadgeAchvRepository.findByRunBadgeIdAndUserId(runBadge.getId(), user.getId());

        // 2. 이미 뱃지를 가지고 있는 경우
        if (existingRunBadgeAchvOP.isPresent()) {
            RunBadgeAchv existingRunBadgeAchv = existingRunBadgeAchvOP.get();
            // 2-1. 새로운 기록이 기존 기록보다 더 좋은지 평가
            if (isNewRecordBetter(newRunRecord, existingRunBadgeAchv.getRunRecord(), runBadge)) {
                // 2-2. 새로운 기록이 더 좋다면, 기존 뱃지 기록 삭제
                runBadgeAchvRepository.delete(existingRunBadgeAchv);
                // 2-3. 새로운 기록으로 뱃지 재부여 및 반환
                return Optional.of(saveRunBadgeAchv(newRunRecord, user, runBadge));
            }
            // 2-4. 새로운 기록이 기존보다 좋지 않다면, 아무것도 하지 않음 (빈 Optional 반환)
        } else {
            // 3. 뱃지를 아직 가지고 있지 않은 경우, 즉시 뱃지 부여
            return Optional.of(saveRunBadgeAchv(newRunRecord, user, runBadge));
        }
        return Optional.empty(); // 뱃지 획득 또는 갱신이 발생하지 않음
    }

    /**
     * <pre>
     * 월간 업적 뱃지(예: 월간 100km 달성)와 같이 단일 기록이 아닌 누적 또는 특정 이벤트를 통해 획득하는 뱃지 유형을 처리합니다.
     * 이 메서드는 단순히 뱃지를 부여하는 역할을 합니다. (획득 조건 검증은 외부 로직에서 수행되었다고 가정)
     * </pre>
     *
     * @param runRecord 현재 달성된 달리기 기록 (주로 뱃지 획득의 트리거가 된 기록)
     * @param user      뱃지를 획득하려는 사용자
     * @param runBadge  획득할 뱃지
     * @return 새로 생성된 RunBadgeAchv를 담은 Optional 객체
     */
    public Optional<RunBadgeAchv> handleMonthlyAchievement(RunRecord runRecord, User user, RunBadge runBadge) {
        // 월간 업적 뱃지는 보통 누적 거리나 횟수 달성 시 부여되므로, 여기서는 별도의 비교 로직 없이 바로 부여한다.
        // (중복 획득 방지는 RunBadgeAchvRepository의 existsByUserAndBadgeAndYearMonth 같은 메서드에서 처리될 수 있다.)
        return Optional.of(saveRunBadgeAchv(runRecord, user, runBadge));
    }

    /**
     * <pre>
     * 새로운 달리기 기록이 기존 뱃지에 연결된 기록보다 더 우수한지 판단합니다.
     * 뱃지 이름에 따라 비교 로직이 달라집니다 (예: 속도는 낮을수록 좋고, 거리는 길수록 좋음).
     * TODO 조건문 처리가 문자열로 하드코딩 되어있는데 어떻게 하드코딩이 아닌 방법으로 처리할지 잘 모르겠다
     * </pre>
     *
     * @param newRunRecord 새로 달성된 RunRecord
     * @param oldRunRecord 기존 뱃지가 가지고 있던 RunRecord
     * @param runBadge     비교 기준이 되는 RunBadge (뱃지 이름에 따라 로직 분기)
     * @return newRecord가 oldRecord보다 우수하면 true, 그렇지 않으면 false
     */
    private boolean isNewRecordBetter(RunRecord newRunRecord, RunRecord oldRunRecord, RunBadge runBadge) {
        // 뱃지 이름에 따라 우수성 판단 기준이 달라진다.
        switch (runBadge.getName()) {
            case "1K 최고 기록":
                // [핵심 로직] '1K 최고 기록' 비교
                // RunRecord 엔티티에 저장된 avgPace 값을 직접 비교합니다.
                // 페이스는 숫자가 낮을수록 더 빠른 기록입니다.
                // newRunRecord.getAvgPace()가 null이 아님을 보장해야 합니다.
                if (newRunRecord.getAvgPace() != null && oldRunRecord.getAvgPace() != null) {
                    return newRunRecord.getAvgPace() < oldRunRecord.getAvgPace();
                }
                return false;
            case "5K 최고 기록":
                // [핵심 로직] '5K 최고 기록' 비교
                // RunRecord 엔티티에 저장된 avgPace 값을 직접 비교합니다.
                // 페이스는 숫자가 낮을수록 더 빠른 기록입니다.
                // newRunRecord.getAvgPace()가 null이 아님을 보장해야 합니다.
                if (newRunRecord.getAvgPace() != null && oldRunRecord.getAvgPace() != null) {
                    return newRunRecord.getAvgPace() < oldRunRecord.getAvgPace();
                }
                return false;
            default:
                // 정의되지 않은 뱃지 이름은 비교 로직 없음
                return false;
        }
    }

    /**
     * 새로운 뱃지 획득 내역(RunBadgeAchv)을 생성하고 데이터베이스에 저장합니다.
     *
     * @param runRecord 뱃지 획득과 관련된 달리기 기록
     * @param user      뱃지를 획득한 사용자
     * @param runBadge  획득된 뱃지
     * @return 저장된 RunBadgeAchv 엔티티
     */
    private RunBadgeAchv saveRunBadgeAchv(RunRecord runRecord, User user, RunBadge runBadge) {
        RunBadgeAchv runBadgeAchv = RunBadgeAchv.builder()
                .runRecord(runRecord) // 뱃지를 획득하게 된 달리기 기록 연결
                .user(user)           // 뱃지를 획득한 사용자 연결
                .runBadge(runBadge)      // 획득된 뱃지 연결
                .build();

        RunBadgeAchv runBadgeAchvPS = runBadgeAchvRepository.save(runBadgeAchv); // DB에 뱃지 획득 내역 저장
        return runBadgeAchvPS;
    }
}
