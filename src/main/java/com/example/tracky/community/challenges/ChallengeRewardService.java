package com.example.tracky.community.challenges;

import com.example.tracky._core.error.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.domain.ChallengeJoin;
import com.example.tracky.community.challenges.domain.RewardMaster;
import com.example.tracky.community.challenges.domain.UserChallengeReward;
import com.example.tracky.community.challenges.enums.ChallengeTypeEnum;
import com.example.tracky.community.challenges.repository.ChallengeJoinRepository;
import com.example.tracky.community.challenges.repository.RewardMasterRepository;
import com.example.tracky.community.challenges.repository.UserChallengeRewardRepository;
import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeRewardService {

    private final ChallengeJoinRepository challengeJoinRepository;
    private final RunRecordRepository runRecordRepository;
    private final UserChallengeRewardRepository userChallengeRewardRepository;
    private final RewardMasterRepository rewardMasterRepository;

    /**
     * 사용자의 러닝 기록을 기반으로 '참여 중인' 챌린지의 달성 여부를 확인하고 보상을 지급합니다.
     *
     * @param user 보상을 받을 사용자
     * @return 새로 획득한 챌린지 보상 목록
     */
    @Transactional
    public List<UserChallengeReward> checkAndAwardChallengeRewards(User user) {
        // 1. 사용자가 참여하고 있는 '진행 중인 챌린지' 참여 정보를 조회합니다.
        List<ChallengeJoin> challengeJoinsPS = challengeJoinRepository.findAllByUserIdAndIsInProgressTrue(user.getId());

        List<UserChallengeReward> newlyAwardedRewards = new ArrayList<>();

        for (ChallengeJoin challengeJoin : challengeJoinsPS) {
            Challenge challenge = challengeJoin.getChallenge();
            boolean alreadyRewarded = false;

            // 2. 챌린지 타입에 따라 중복 확인 로직을 분기합니다.
            if (challenge.getType() == ChallengeTypeEnum.PUBLIC) {
                // 공개 챌린지: "챌린지 이름"과 "보상 이름"이 같은 보상을 받았는지 확인
                alreadyRewarded = userChallengeRewardRepository.existsPublicRewardByChallengeName(user.getId(), challenge.getId(), challenge.getName());

            } else if (challenge.getType() == ChallengeTypeEnum.PRIVATE) {
                // "완주자" 보상을 조회
                RewardMaster rewardMasterOP = rewardMasterRepository.findByRewardName("완주자")
                        .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.REWARD_MASTER_NOT_FOUND));
                // ID를 파라미터로 받는 새로운 메서드를 호출합니다.
                alreadyRewarded = userChallengeRewardRepository.existsPrivateRewardByRewardId(user.getId(), challenge.getId(), rewardMasterOP.getId());
            }

            if (alreadyRewarded) {
                continue; // 이미 보상을 받았다면, 다음 챌린지로 넘어갑니다.
            }

            // 3. 챌린지 기간 동안의 누적 거리를 조회합니다.
            Integer totalDistanceInChallenge = runRecordRepository
                    .findTotalDistanceByUserIdAndDateRange(user.getId(), challenge.getStartDate(), challenge.getEndDate());

            // 4. 누적 거리가 챌린지 목표 거리를 달성했는지 확인합니다.
            if (totalDistanceInChallenge >= challenge.getTargetDistance()) {
                // 5. 목표 달성! 챌린지 타입에 따라 보상을 생성합니다.
                UserChallengeReward newReward = null;

                if (challenge.getType() == ChallengeTypeEnum.PUBLIC) {
                    // 공개 챌린지: 챌린지 이름과 동일한 이름의 보상을 찾습니다.
                    RewardMaster rewardMasterPS = rewardMasterRepository.findByRewardName(challenge.getName())
                            .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.REWARD_MASTER_NOT_FOUND)); // 보상이 없으면 에러

                    // UserChallengeReward 엔티티를 생성합니다.
                    newReward = UserChallengeReward.builder()
                            .user(user)
                            .challenge(challenge)
                            .rewardMaster(rewardMasterPS)
                            .type(ChallengeTypeEnum.PUBLIC)
                            .build();

                } else if (challenge.getType() == ChallengeTypeEnum.PRIVATE) {
                    // 사설 챌린지: '완주자' 보상을 찾습니다.
                    final String PARTICIPATION_REWARD_NAME = "완주자";
                    RewardMaster rewardMasterPS = rewardMasterRepository.findByRewardName(PARTICIPATION_REWARD_NAME)
                            .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.REWARD_MASTER_NOT_FOUND));

                    newReward = UserChallengeReward.builder()
                            .user(user)
                            .challenge(challenge)
                            .rewardMaster(rewardMasterPS)
                            .type(ChallengeTypeEnum.PRIVATE)
                            .build();
                }

                // 6. 생성된 보상이 있다면, DB에 저장하고 결과 리스트에 추가합니다.
                if (newReward != null) {
                    UserChallengeReward savedReward = userChallengeRewardRepository.save(newReward);
                    newlyAwardedRewards.add(savedReward);
                }
            }
        }

        return newlyAwardedRewards;
    }
}
