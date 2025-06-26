package com.example.tracky.runrecord.runbadge.runbadgeachv.handler;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchv;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvRepository;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 뱃지 획득과 관련된 세부적인 생성, 삭제, 비교 로직을 처리하는 핸들러 클래스입니다.
 * 이 클래스는 Spring 컴포넌트로 등록되어 의존성 주입을 통해 Repository를 사용합니다.
 */
@Component
@RequiredArgsConstructor
public class RunBadgeAchvHandler {

    private final RunBadgeAchvRepository runBadgeAchvRepository;

    /**
     * '기록' 타입 뱃지를 처리합니다. (예: 개인 최고 기록)
     * 기존 기록이 있으면 비교 후 갱신(삭제 후 생성)하고, 없으면 새로 생성합니다.
     *
     * @param newRunRecord 새로 추가된 달리기 기록
     * @param user         현재 사용자
     * @param badge        처리할 뱃지 정보
     * @return 새로 생성된 RunBadgeAchv 엔티티가 담긴 Optional, 변경이 없으면 비어있는 Optional
     */
    public Optional<RunBadgeAchv> handleRecordBadge(RunRecord newRunRecord, User user, RunBadge badge) {
        // 이 유저가 해당 '기록' 뱃지를 이미 보유하고 있는지 조회합니다.
        Optional<RunBadgeAchv> existingAchvOpt = runBadgeAchvRepository.findByRunBadgeAndRunRecord_User(badge, user);

        if (existingAchvOpt.isPresent()) {
            // 이미 뱃지를 보유한 기록이 있다면, 기록 갱신 여부를 판단합니다.
            RunBadgeAchv existingAchv = existingAchvOpt.get();
            if (isNewRecordBetter(newRunRecord, existingAchv.getRunRecord(), badge)) {
                // 새로운 기록이 더 좋으므로, 기존 뱃지 획득 내역은 삭제합니다.
                runBadgeAchvRepository.delete(existingAchv);
                // 새로운 기록에 뱃지를 부여하고, 생성된 엔티티를 반환합니다.
                return Optional.of(awardBadgeToRecord(newRunRecord, badge));
            }
        } else {
            // 이 '기록' 뱃지를 처음 획득하는 경우, 바로 부여하고 생성된 엔티티를 반환합니다.
            return Optional.of(awardBadgeToRecord(newRunRecord, badge));
        }
        // 기록이 갱신되지 않은 경우, 아무것도 반환하지 않습니다.
        return Optional.empty();
    }

    /**
     * '업적' 타입 뱃지를 처리합니다.
     * 사용자가 해당 뱃지를 보유하고 있지 않을 때만 새로 부여합니다.
     *
     * @param newRunRecord 뱃지 획득의 계기가 된 달리기 기록
     * @param user         현재 사용자
     * @param badge        처리할 뱃지 정보
     * @return 새로 생성된 RunBadgeAchv 엔티티가 담긴 Optional, 이미 보유했다면 비어있는 Optional
     */
    public Optional<RunBadgeAchv> handleAchievementBadge(RunRecord newRunRecord, User user, RunBadge badge) {
        // 이 유저가 이 '업적' 뱃지를 이미 획득했는지 확인합니다.
        if (!runBadgeAchvRepository.existsByRunBadgeAndRunRecord_User(badge, user)) {
            // 획득한 적이 없다면, 현재 기록에 뱃지를 부여하고 생성된 엔티티를 반환합니다.
            return Optional.of(awardBadgeToRecord(newRunRecord, badge));
        }
        // 이미 획득한 뱃지이므로, 아무것도 반환하지 않습니다.
        return Optional.empty();
    }

    /**
     * 새로운 기록이 기존 기록보다 더 나은지 판단하는 내부 헬퍼 메서드입니다.
     *
     * @param newRecord 새로 추가된 달리기 기록
     * @param oldRecord 기존에 뱃지를 보유하고 있던 달리기 기록
     * @param badge     비교 대상 뱃지
     * @return 새로운 기록이 더 좋으면 true
     */
    private boolean isNewRecordBetter(RunRecord newRecord, RunRecord oldRecord, RunBadge badge) {
        switch (badge.getName()) {
            case "1K 최고 기록":
                // 1km 평균 페이스(초/km)를 계산하여 비교합니다. 페이스는 낮을수록 좋습니다.
                // 0으로 나누는 오류를 방지하기 위해 거리가 0보다 큰지 확인합니다.
                if (newRecord.getTotalDistanceMeters() > 0 && oldRecord.getTotalDistanceMeters() > 0) {
                    double newPace = (double) newRecord.getTotalDurationSeconds() / (newRecord.getTotalDistanceMeters() / 1000.0);
                    double oldPace = (double) oldRecord.getTotalDurationSeconds() / (oldRecord.getTotalDistanceMeters() / 1000.0);
                    return newPace < oldPace;
                }
                return false;

            case "5K 최고 기록":
                // 5km 이상 달린 기록들 중에서 총 시간이 가장 짧은 것을 최고 기록으로 판단합니다.
                return newRecord.getTotalDurationSeconds() < oldRecord.getTotalDurationSeconds();

            default:
                return false;
        }
    }

    /**
     * RunBadgeAchv 엔티티를 생성하고 저장 후, 저장된 엔티티를 반환하는 공통 메서드입니다.
     *
     * @param runRecord 뱃지가 연결될 달리기 기록
     * @param badge     획득할 뱃지
     * @return 데이터베이스에 저장된 RunBadgeAchv 엔티티
     */
    private RunBadgeAchv awardBadgeToRecord(RunRecord runRecord, RunBadge badge) {
        RunBadgeAchv newAchievement = RunBadgeAchv.builder()
                .runRecord(runRecord)
                .runBadge(badge)
                .build();
        runBadgeAchvRepository.save(newAchievement);
        return newAchievement;
    }
}
