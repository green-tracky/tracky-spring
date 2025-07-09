package com.example.tracky.community.challenges;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.domain.ChallengeJoin;
import com.example.tracky.community.challenges.dto.ChallengeJoinResponse;
import com.example.tracky.community.challenges.repository.ChallengeJoinRepository;
import com.example.tracky.community.challenges.repository.ChallengeRepository;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChallengeJoinService {
    private final ChallengeJoinRepository challengeJoinRepository;
    private final ChallengeRepository challengeRepository;

    /**
     * 챌린지 참여
     *
     * @param id
     * @param user
     * @return
     */
    @Transactional
    public ChallengeJoinResponse.DTO join(Integer id, User user) {
        // 1. 챌린지 조회
        Challenge challengePS = challengeRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.CHALLENGE_NOT_FOUND));

        // 2. 진행중인지 확인
        if (!challengePS.getIsInProgress()) {
            throw new ExceptionApi400(ErrorCodeEnum.CHALLENGE_ALREADY_ENDED);
        }

        // 3. 챌린지참가 엔티티 생성
        ChallengeJoin challengeJoin = ChallengeJoin.builder()
                .challenge(challengePS)
                .user(user)
                .build();

        // 4. 챌린지 참가 엔티티 저장
        ChallengeJoin challengeJoinPS = challengeJoinRepository.save(challengeJoin);

        // 5. 챌린지 참가 엔티티 응답
        return new ChallengeJoinResponse.DTO(challengeJoinPS);
    }

    @Transactional
    public void leave(Integer id, User user) {
        // 내가 참여한 챌린지 조회
        ChallengeJoin challengeJoinPS = challengeJoinRepository.findByChallengeIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.CHALLENGE_JOIN_NOT_FOUND));

        // 삭제
        challengeJoinRepository.delete(challengeJoinPS);
    }

}
