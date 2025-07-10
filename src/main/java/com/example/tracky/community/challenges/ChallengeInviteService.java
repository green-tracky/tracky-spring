package com.example.tracky.community.challenges;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.enums.InviteStatusEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.domain.ChallengeInvite;
import com.example.tracky.community.challenges.dto.ChallengeInviteResponse;
import com.example.tracky.community.challenges.repository.ChallengeInviteRepository;
import com.example.tracky.community.challenges.repository.ChallengeRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChallengeInviteService {

    private final ChallengeInviteRepository challengeInviteRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;


    @Transactional
    public ChallengeInviteResponse.saveDTO challengesInvite(Integer id, Integer userId, User user) {
        // 초대할 유저 조회
        User toUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 초대할 챌린지 조회
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.CHALLENGE_NOT_FOUND));

        ChallengeInvite Invite = ChallengeInvite.builder()
                .fromUser(user)
                .toUser(toUser)
                .challenge(challenge)
                .status(InviteStatusEnum.PENDING)
                .build();

        ChallengeInvite savePS = challengeInviteRepository.save(Invite);

        return new ChallengeInviteResponse.saveDTO(savePS);
    }
}