package com.example.tracky.community.challenges;

import com.example.tracky._core.enums.ChallengeTypeEnum;
import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky._core.values.TimeValue;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.domain.ChallengeJoin;
import com.example.tracky.community.challenges.domain.RewardMaster;
import com.example.tracky.community.challenges.domain.UserChallengeReward;
import com.example.tracky.community.challenges.repository.ChallengeJoinRepository;
import com.example.tracky.community.challenges.repository.ChallengeRepository;
import com.example.tracky.community.challenges.repository.RewardMasterRepository;
import com.example.tracky.community.challenges.repository.UserChallengeRewardRepository;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeStatusService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeJoinRepository challengeJoinRepository;
    private final RunRecordRepository runRecordRepository;
    private final RewardMasterRepository rewardMasterRepository;
    private final UserChallengeRewardRepository userChallengeRewardRepository;

    /**
     * <pre>
     * 진행중인 챌린지 중 날짜가 지난 챌린지를 종료한다
     * 사설챌린지는 금, 은, 동 보상을 지급한다
     * </pre>
     */
    @Transactional
    public void closeAndRewardChallenges() {
        LocalDateTime now = TimeValue.getServerTime();
        log.debug("날짜" + now);

        // 1. 종료 대상 챌린지 조회 (진행중이고, 종료일이 현재보다 이전)
        List<Challenge> toCloseChallengesPS = challengeRepository.findAllByIsInProgressTrueAndEndDateBefore(now);
        log.debug("테스트 챌린지 확인" + toCloseChallengesPS.getLast().getName());

        for (Challenge challenge : toCloseChallengesPS) {
            // 2. 챌린지 상태 변경
            challenge.closeChallenge();

            // 3. 사설 챌린지라면 메달 지급
            if (challenge.getType() == ChallengeTypeEnum.PRIVATE) {
                log.debug("사설챌린지 확인 : " + challenge.getName());
                rewardPrivateChallengeMedals(challenge);
            }
        }
    }

    /**
     * 사설 챌린지 보상 지급(금,은,동)
     *
     * @param challenge
     */
    private void rewardPrivateChallengeMedals(Challenge challenge) {
        // 현재 챌린지의 모든 참가자 조회
        List<ChallengeJoin> challengeJoinsPS = challengeJoinRepository.findAllByChallengeId(challenge.getId());
        challengeJoinsPS.forEach(join -> {
        });

        // 등수 확인을 위한 리스트
        List<MedalInfo> achievers = new ArrayList<>();

        // 참가자의 챌린지 기간내의 모든 러닝 조회
        for (ChallengeJoin join : challengeJoinsPS) {
            List<RunRecord> recordsPS = runRecordRepository.findAllByCreatedAtBetween(
                    join.getUser().getId(), challenge.getStartDate(), challenge.getEndDate());

            if (recordsPS.isEmpty()) continue; // 없으면 다음 참가자 계산

            // 챌린지 기간내의 첫 러닝 기록날짜
            LocalDateTime startAt = recordsPS.get(0).getCreatedAt();

            // 챌린지 목표달성 경과 시간 계산
            int sum = 0;
            for (RunRecord record : recordsPS) {
                sum += record.getTotalDistanceMeters();
                if (sum >= challenge.getTargetDistance()) {
                    LocalDateTime endAt = record.getCreatedAt();
                    long durationSeconds = Duration.between(startAt, endAt).getSeconds();
                    // 임시 데이터 클래스에 유저, 러닝 시작날짜, 러닝
                    achievers.add(new MedalInfo(join.getUser(), startAt, endAt, durationSeconds));
                    break;
                }
            }
        }

        // 달성 시간(초)이 짧은 순서로 정렬
        achievers.sort(Comparator.comparingLong(m -> m.durationSeconds));

        // 지급해야할 메달 종류
        String[] medals = {"금메달", "은메달", "동메달"};
        // 금, 은, 동 으로 최대 3번 반복한다
        for (int i = 0; i < Math.min(3, achievers.size()); i++) {
            User user = achievers.get(i).user;
            RewardMaster medalRewardPS = rewardMasterRepository.findByRewardName(medals[i])
                    .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.REWARD_MASTER_NOT_FOUND));

            UserChallengeReward reward = UserChallengeReward.builder()
                    .user(user)
                    .challenge(challenge)
                    .rewardMaster(medalRewardPS)
                    .type(ChallengeTypeEnum.PRIVATE)
                    .build();
            userChallengeRewardRepository.save(reward);

            log.info("챌린지 참가자 전원에게 보상이 지급되었습니다.");
        }
    }

    /**
     * 참가자가 없는 사설 챌린지 삭제
     */
    @Transactional
    public void cleanupEmptyPrivateChallenges() {
        // 1. 참가자가 0명인, '진행 중인' 모든 사설 챌린지를 조회
        List<Challenge> emptyChallengesPS = challengeRepository.findOngoingEmptyPrivateChallenges();

        // 2. 조회된 챌린지가 있다면, DB에서 삭제
        if (emptyChallengesPS != null && !emptyChallengesPS.isEmpty()) {
            challengeRepository.deleteAllEmptyChallenge(emptyChallengesPS);
        }

        log.info("참가자가 없는 챌린지가 삭제되었습니다.");
    }

    // 비교 로직에 필요한 임시 데이터 클래스
    class MedalInfo {
        User user;
        LocalDateTime startAt;   // 첫 러닝 기록 날짜
        LocalDateTime endAt;     // 목표 달성한 러닝 기록 날짜
        long durationSeconds;    // 달성까지 걸린 시간(초)

        MedalInfo(User user, LocalDateTime startAt, LocalDateTime endAt, long durationSeconds) {
            this.user = user;
            this.startAt = startAt;
            this.endAt = endAt;
            this.durationSeconds = durationSeconds;
        }
    }
}

