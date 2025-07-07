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

    // 매일 00:00:00에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void closeAndRewardChallenges() {
        log.info("[챌린지 스케줄러] 종료/메달 지급 시작");
        challengeStatusService.closeAndRewardChallenges();
        log.info("[챌린지 스케줄러] 종료/메달 지급 완료");
    }
}