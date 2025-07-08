package com.example.tracky.community.challenges;

import com.example.tracky._core.error.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky._core.value.TimeValue;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.domain.ChallengeJoin;
import com.example.tracky.community.challenges.domain.RewardMaster;
import com.example.tracky.community.challenges.dto.ChallengeResponse;
import com.example.tracky.community.challenges.enums.ChallengeTypeEnum;
import com.example.tracky.community.challenges.repository.ChallengeJoinRepository;
import com.example.tracky.community.challenges.repository.ChallengeRepository;
import com.example.tracky.community.challenges.repository.RewardMasterRepository;
import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeJoinRepository challengeJoinRepository;
    private final RunRecordRepository runRecordRepository;
    private final RewardMasterRepository rewardMasterRepository;

    /**
     * 챌린지 목록 보기
     *
     * @param user
     * @return
     */
    public ChallengeResponse.MainDTO getChallenges(User user) {
        Integer userId = user.getId();
        LocalDateTime now = TimeValue.getServerTime(); // 조회 시점

        // 1. 사용자가 참가한 챌린지 엔티티 목록 조회
        // ChallengeJoin 테이블을 통해, 현재 유저가 참가한 Challenge 엔티티들을 가져온다
        List<ChallengeJoin> challengeJoinsPS = challengeJoinRepository.findAllByUserIdJoin(userId);
        List<Challenge> joinedChallengesPS = challengeJoinsPS.stream()
                .map(challengeJoin -> challengeJoin.getChallenge())
                .toList();

        // 2. 참여 가능한 공식 챌린지 엔티티 목록 조회
        // 먼저, 참가한 챌린지들의 ID 목록을 효율적으로 가져온다
        Set<Integer> joinedChallengeIds = challengeJoinRepository.findChallengeIdsByUserId(userId); // 물음: 1번에서 조회를 했는데 또 해야하나? -> 각각의 필요한 로직은 따로 분리해서 처리하는게 좋다. 1번 역할에 필요한것 따로 2번 역할에 필요한것 따로 -> 나중에 쿼리가 많아지면 그때 수정한다
        // 이 ID 목록을 제외하고, 아직 진행 중인 공식 챌린지들을 조회한다. now -> 조회 조건용
        List<Challenge> unjoinedChallengesPS = challengeRepository.findUnjoinedPublicChallenges(joinedChallengeIds, now);

        // 3. 챌린지별 누적 달리기 거리 계산 (Map)
        // 참가한 각 챌린지에 대해, 기간 내 누적 거리를 계산하여 Map에 저장한다
        Map<Integer, Integer> totalDistancesMap = joinedChallengesPS.stream()
                .collect(Collectors.toMap(
                        challenge -> challenge.getId(),
                        challenge -> runRecordRepository.findTotalDistanceByUserIdAndDateRange(
                                userId,
                                challenge.getStartDate(),
                                challenge.getEndDate()
                        )
                ));

        // 4. 챌린지별 참가자 수 계산 (Map)
        // 참여 가능한 각 챌린지에 대해, 참가자 수를 계산하여 Map에 저장한다
        Map<Integer, Integer> participantCountsMap = unjoinedChallengesPS.stream()
                .collect(Collectors.toMap(
                        challenge -> challenge.getId(),
                        challenge -> challengeJoinRepository.countByChallengeId(challenge.getId())
                ));

        // 5. 모든 재료를 MainDTO 생성자에게 전달
        return new ChallengeResponse.MainDTO(
                joinedChallengesPS,
                unjoinedChallengesPS,
                totalDistancesMap,
                participantCountsMap
        );
    }

    /**
     * 챌린지 상세보기
     *
     * @param id   challengeId
     * @param user
     * @return
     */
    public ChallengeResponse.DetailDTO getChallenge(Integer id, User user) {
        // 1. 챌린지 엔티티 조회 (공식/사설 구분)
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.CHALLENGE_NOT_FOUND));

        // 2. 참가자 수 조회
        int participantCount = challengeJoinRepository.countByChallengeId(id);

        // 3. 내 참가 여부
        boolean isJoined = challengeJoinRepository.existsByUserIdAndChallengeId(user.getId(), id);

        // 4. 내 누적 거리, 내 순위 (참가자만)
        Integer myDistance = null;
        Integer myRank = null;
        if (isJoined) {
            myDistance = runRecordRepository.findTotalDistanceByUserIdAndDateRange(
                    user.getId(),
                    challenge.getStartDate(),
                    challenge.getEndDate()
            );
            myRank = challengeJoinRepository.findRankByChallengeIdAndUserId(id, user.getId());
        }

        // 5. 리워드 정보 (공식/사설 모두 RewardMaster에서 조회)
        List<RewardMaster> rewardMasters;
        if (challenge.getType() == ChallengeTypeEnum.PRIVATE) {
            // 사설 챌린지: type이 사설인 모든 리워드
            rewardMasters = rewardMasterRepository.findAllByType(ChallengeTypeEnum.PRIVATE);
        } else {
            // 공개 챌린지: 챌린지 이름과 rewardName이 동일한 리워드
            rewardMasters = rewardMasterRepository.findAllByRewardName(challenge.getName());
        }

        // 6. DTO 조립
        return new ChallengeResponse.DetailDTO(
                challenge,
                participantCount,
                myDistance,
                myRank,
                isJoined,
                rewardMasters
        );
    }
}