package com.example.tracky.community.challenges;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChallengeScheduler {

    private final ChallengeStatusService challengeStatusService;
    private final ChallengeCreationService challengeCreationService;

    // 매일 00:00:00에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void closeAndRewardChallenges() {
        log.info("[챌린지 스케줄러] 종료/메달 지급 시작");
        challengeStatusService.closeAndRewardChallenges();
        log.info("[챌린지 스케줄러] 종료/메달 지급 완료");
    }

    // 매주 월요일 00:00:10에 실행 (상태 변경 스케줄러와 약간의 시간차를 둠)
    @Scheduled(cron = "10 0 0 * * MON")
    public void createWeeklyChallenges() {
        log.info("[챌린지 스케줄러] 주간 챌린지 생성 시작");
        challengeCreationService.createWeeklyChallenges();
        log.info("[챌린지 스케줄러] 주간 챌린지 생성 종료");
    }

    // 매월 1일 00:00:20에 실행
    @Scheduled(cron = "20 0 0 1 * *")
    public void createMonthlyChallenges() {
        log.info("[챌린지 스케줄러] 월간 챌린지 생성 시작");
        challengeCreationService.createMonthlyChallenges();
        log.info("[챌린지 스케줄러] 월간 챌린지 생성 종료");
    }

    // 매일 00:01:00에 미참여 사설 챌린지 정리 로직 실행 (약간의 시간차)
    @Scheduled(cron = "0 1 0 * * *")
    public void cleanupEmptyChallenges() {
        log.info("[챌린지 스케줄러] 미참여 사설 챌린지 정리 시작");
        challengeStatusService.cleanupEmptyPrivateChallenges();
        log.info("[챌린지 스케줄러] 미참여 사설 챌린지 정리 완료");
    }
}