package com.example.tracky.runrecord.runbadges;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.challenges.domain.UserChallengeReward;
import com.example.tracky.community.challenges.repository.UserChallengeRewardRepository;
import com.example.tracky.runrecord.runbadges.runbadgeachvs.RunBadgeAchv;
import com.example.tracky.runrecord.runbadges.runbadgeachvs.RunBadgeAchvRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunBadgeService {

    private final UserRepository userRepository;
    private final RunBadgeRepository runBadgeRepository;
    private final RunBadgeAchvRepository runBadgeAchvRepository;
    private final UserChallengeRewardRepository userChallengeRewardRepository;

    /**
     * 보상목록 보기
     *
     * @param sessionProfile
     * @return
     */
    public RunBadgeResponse.GroupedBadgeListDTO getRunBadges(OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 1. 조회
        List<RunBadge> runBadgesPS = runBadgeRepository.findAll();
        List<RunBadgeAchv> runBadgeAchvsPS = runBadgeAchvRepository.findByUserIdJoin(userPS.getId());
        List<UserChallengeReward> userChallengeRewardsPS = userChallengeRewardRepository.findAllByUserId(userPS.getId());

        log.info("{}({})이 메달 목록을 조회합니다.", userPS.getUsername(), userPS.getId());

        // 2. 응답 DTO 로 변환
        return new RunBadgeResponse.GroupedBadgeListDTO(runBadgesPS, runBadgeAchvsPS, userChallengeRewardsPS);
    }

}