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
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeJoinService {
    private final ChallengeJoinRepository challengeJoinRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    /**
     * 챌린지 참여
     *
     * @param id
     * @param user
     * @return
     */
    @Transactional
    public ChallengeJoinResponse.DTO join(Integer id, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

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
                .user(userPS)
                .build();

        // 4. 챌린지 참가 엔티티 저장
        ChallengeJoin challengeJoinPS = challengeJoinRepository.save(challengeJoin);

        log.info("{}({})이 챌린지 {}({})에 참가하였습니다.", userPS.getUsername(), userPS.getId(), challengePS.getName(), challengePS.getId());

        // 5. 챌린지 참가 엔티티 응답
        return new ChallengeJoinResponse.DTO(challengeJoinPS);
    }

    @Transactional
    public void leave(Integer id, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 내가 참여한 챌린지 조회
        ChallengeJoin challengeJoinPS = challengeJoinRepository.findByChallengeIdAndUserId(id, userPS.getId())
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.CHALLENGE_JOIN_NOT_FOUND));

        log.info("{}({})이 챌린지 {}({})에서 떠났습니다.", userPS.getUsername(), userPS.getId(), challengeJoinPS.getChallenge().getName(), challengeJoinPS.getChallenge().getId());

        // 삭제
        challengeJoinRepository.delete(challengeJoinPS);
    }

}
