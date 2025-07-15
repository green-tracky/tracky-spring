package com.example.tracky.community.challenges;

import com.example.tracky._core.enums.ChallengeTypeEnum;
import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky._core.values.TimeValue;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.domain.ChallengeInvite;
import com.example.tracky.community.challenges.domain.ChallengeJoin;
import com.example.tracky.community.challenges.domain.RewardMaster;
import com.example.tracky.community.challenges.dto.ChallengeRequest;
import com.example.tracky.community.challenges.dto.ChallengeResponse;
import com.example.tracky.community.challenges.repository.ChallengeInviteRepository;
import com.example.tracky.community.challenges.repository.ChallengeJoinRepository;
import com.example.tracky.community.challenges.repository.ChallengeRepository;
import com.example.tracky.community.challenges.repository.RewardMasterRepository;
import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeJoinRepository challengeJoinRepository;
    private final ChallengeInviteRepository challengeInviteRepository;
    private final RunRecordRepository runRecordRepository;
    private final RewardMasterRepository rewardMasterRepository;
    private final UserRepository userRepository;

    /**
     * 챌린지 목록 보기
     *
     * @param sessionProfile
     * @return
     */
    public ChallengeResponse.MainDTO getChallenges(OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Integer userId = userPS.getId();
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

        // 5. 초대받은 챌린지 목록 조회
        List<ChallengeInvite> inviteChallengesPS = challengeInviteRepository.findAllToUserIdJoin(userPS.getId());

        log.info("{}({})이 전체 챌린지를 조회합니다.", userPS.getUsername(), userPS.getId());

        // 6. 모든 재료를 MainDTO 생성자에게 전달
        return new ChallengeResponse.MainDTO(
                joinedChallengesPS,
                unjoinedChallengesPS,
                totalDistancesMap,
                participantCountsMap,
                inviteChallengesPS
        );
    }

    /**
     * 챌린지 상세보기
     *
     * @param id             challengeId
     * @param sessionProfile
     * @return
     */
    public ChallengeResponse.DetailDTO getChallenge(Integer id, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 1. 챌린지 엔티티 조회
        Challenge challengePS = challengeRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.CHALLENGE_NOT_FOUND));

        // 2. 참가자 수 조회
        int participantCount = challengeJoinRepository.countByChallengeId(id);

        // 3. 내 참가 여부
        boolean isJoined = challengeJoinRepository.existsByUserIdAndChallengeId(userPS.getId(), id);

        // 4. 내 누적 거리, 내 순위 (참가자만)
        Integer myDistance = null;
        Integer myRank = null;
        if (isJoined) {
            myDistance = runRecordRepository.findTotalDistanceByUserIdAndDateRange(
                    userPS.getId(),
                    challengePS.getStartDate(),
                    challengePS.getEndDate()
            );
            myRank = challengeJoinRepository.findRankByChallengeIdAndUserId(id, userPS.getId());
        }

        // 5. 리워드 정보 (공식/사설 모두 리스트로 조회)
        List<RewardMaster> rewardMasters;
        if (challengePS.getType() == ChallengeTypeEnum.PRIVATE) {
            // 사설 챌린지: type이 사설인 모든 리워드
            rewardMasters = rewardMasterRepository.findAllByType(ChallengeTypeEnum.PRIVATE);
        } else {
            // 공개 챌린지: 챌린지 이름과 rewardName이 동일한 리워드
            rewardMasters = rewardMasterRepository.findAllByRewardName(challengePS.getName());
        }

        log.info("{}({})이 챌린지{}({})을 상세보기합니다.", userPS.getUsername(), userPS.getId(), challengePS.getName(), challengePS.getId());

        // 6. DTO 조립
        return new ChallengeResponse.DetailDTO(
                challengePS,
                participantCount,
                myDistance,
                myRank,
                isJoined,
                rewardMasters
        );
    }

    @Transactional
    public ChallengeResponse.SaveDTO save(OAuthProfile sessionProfile, ChallengeRequest.SaveDTO reqDTO) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 1. DTO를 사용하여 챌린지 엔티티를 생성
        Challenge challenge = reqDTO.toEntity(userPS);
        Challenge challengePS = challengeRepository.save(challenge);

        // 2. 생성자를 챌린지에 바로 참여
        ChallengeJoin join = ChallengeJoin.builder()
                .user(userPS)
                .challenge(challengePS)
                .build();
        challengeJoinRepository.save(join);

        log.info("{}({})이 챌린지{}({})를 생성했습니다.", userPS.getUsername(), userPS.getId(), challengePS.getName(), challengePS.getId());

        // 3. 생성된 챌린지 정보를 담은 응답 DTO를 반환
        return new ChallengeResponse.SaveDTO(challengePS);
    }

    public ChallengeResponse.UpdateDTO update(Integer id, OAuthProfile sessionProfile, ChallengeRequest.UpdateDTO reqDTO) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 챌린지 조회
        Challenge challengePS = challengeRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.CHALLENGE_NOT_FOUND));

        if (!challengePS.getCreator().getId().equals(userPS.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }

        challengePS.updateName(reqDTO);

        log.info("{}({})이 챌린지{}({})를 수정했습니다.", userPS.getUsername(), userPS.getId(), challengePS.getName(), challengePS.getId());

        return new ChallengeResponse.UpdateDTO(challengePS);
    }
}