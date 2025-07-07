package com.example.tracky.community.challenges;

import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.domain.ChallengeJoin;
import com.example.tracky.community.challenges.domain.RewardMaster;
import com.example.tracky.community.challenges.domain.UserChallengeReward;
import com.example.tracky.community.challenges.enums.ChallengeTypeEnum;
import com.example.tracky.community.challenges.repository.ChallengeJoinRepository;
import com.example.tracky.community.challenges.repository.ChallengeRepository;
import com.example.tracky.community.challenges.repository.RewardMasterRepository;
import com.example.tracky.community.challenges.repository.UserChallengeRewardRepository;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
        LocalDateTime now = LocalDateTime.now();

        // 1. 종료 대상 챌린지 조회 (진행중이고, 종료일이 현재보다 이전)
        List<Challenge> toCloseChallengesPS = challengeRepository.findAllByIsInProgressTrueAndEndDateBefore(now);

        for (Challenge challenge : toCloseChallengesPS) {
            // 2. 챌린지 상태 변경
            challenge.closeChallenge();

            // 3. 사설 챌린지라면 메달 지급
            if (challenge.getType() == ChallengeTypeEnum.PRIVATE) {
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
        // 등수 확인을 위한 리스트
        List<MedalInfo> achievers = new ArrayList<>();

        // 참가자의 챌린지 기간내의 모든 러닝 조회
        for (ChallengeJoin join : challengeJoinsPS) {
            List<RunRecord> records = runRecordRepository.findAllByCreatedAtBetween(
                    join.getUser().getId(), challenge.getStartDate(), challenge.getEndDate());

            if (records.isEmpty()) continue; // 없으면 다음 참가자 계산

            // 챌린지 기간내의 첫 러닝 기록날짜
            LocalDateTime startAt = records.get(0).getCreatedAt();

            // 챌린지 목표달성 확인을 위한 합계변수
            int sum = 0;
            for (RunRecord record : records) {
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
            Optional<RewardMaster> medalReward = rewardMasterRepository.findByRewardName(medals[i]);
            if (medalReward.isPresent()) {
                boolean alreadyRewarded = userChallengeRewardRepository.existsPrivateRewardByRewardId(
                        user.getId(), challenge.getId(), medalReward.get().getId());
                if (!alreadyRewarded) {
                    UserChallengeReward reward = UserChallengeReward.builder()
                            .user(user)
                            .challenge(challenge)
                            .rewardMaster(medalReward.get())
                            .type(ChallengeTypeEnum.PRIVATE)
                            .build();
                    userChallengeRewardRepository.save(reward);
                }
            }
        }
    }
}

