package com.example.tracky.community.challenge;

import com.example.tracky.community.challenge.domain.Challenge;
import com.example.tracky.community.challenge.domain.ChallengeJoin;
import com.example.tracky.community.challenge.domain.PublicChallenge;
import com.example.tracky.community.challenge.dto.ChallengeResponse;
import com.example.tracky.community.challenge.repository.ChallengeJoinRepository;
import com.example.tracky.community.challenge.repository.ChallengeRepository;
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

    public ChallengeResponse.MainDTO getChallenges(User user) {
        Integer userId = user.getId();
        LocalDateTime now = LocalDateTime.now(); // 조회 시점

        // 1. [재료 준비] 사용자가 참가한 챌린지 엔티티 목록 조회
        // ChallengeJoin 테이블을 통해, 현재 유저가 참가한 Challenge 엔티티들을 가져옵니다.
        List<ChallengeJoin> challengeJoinsPS = challengeJoinRepository.findAllByUserIdJoin(userId);
        List<Challenge> joinedChallengesPS = challengeJoinsPS.stream()
                .map(challengeJoin -> challengeJoin.getChallenge())
                .toList();

        // 2. [재료 준비] 참여 가능한 공식 챌린지 엔티티 목록 조회
        // 먼저, 참가한 챌린지들의 ID 목록을 효율적으로 가져옵니다.
        Set<Integer> joinedChallengeIds = challengeJoinRepository.findChallengeIdsByUserId(userId); // 물음: 1번에서 조회를 했는데 또 해야하나?
        // 이 ID 목록을 제외하고, 아직 진행 중인 공식 챌린지들을 조회합니다. now -> 조회 조건용
        List<PublicChallenge> unjoinedChallengesPS = challengeRepository.findUnjoinedPublicChallenges(joinedChallengeIds, now);

        // 3. [재료 준비] 챌린지별 누적 달리기 거리 계산 (Map)
        // 참가한 각 챌린지에 대해, 기간 내 누적 거리를 계산하여 Map에 저장합니다.
        Map<Integer, Integer> totalDistancesMap = joinedChallengesPS.stream()
                .collect(Collectors.toMap(
                        challenge -> challenge.getId(),
                        challenge -> runRecordRepository.findTotalDistanceByUserIdAndDateRange(
                                userId,
                                challenge.getStartDate(),
                                challenge.getEndDate()
                        )
                ));

        // 4. [재료 준비] 챌린지별 참가자 수 계산 (Map)
        // 참여 가능한 각 챌린지에 대해, 참가자 수를 계산하여 Map에 저장합니다.
        Map<Integer, Integer> participantCountsMap = unjoinedChallengesPS.stream()
                .collect(Collectors.toMap(
                        publicChallenge -> publicChallenge.getId(),
                        challenge -> challengeJoinRepository.countByChallengeId(challenge.getId())
                ));

        // 5. [최종 조립] 모든 재료를 MainDTO 생성자에게 전달
        // DTO 내부에서 모든 가공 로직이 처리됩니다.
        return new ChallengeResponse.MainDTO(
                joinedChallengesPS,
                unjoinedChallengesPS,
                totalDistancesMap,
                participantCountsMap
        );
    }
}