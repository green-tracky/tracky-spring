package com.example.tracky.community.challenges;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.enums.InviteStatusEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.domain.ChallengeInvite;
import com.example.tracky.community.challenges.dto.ChallengeInviteRequest;
import com.example.tracky.community.challenges.dto.ChallengeInviteResponse;
import com.example.tracky.community.challenges.repository.ChallengeInviteRepository;
import com.example.tracky.community.challenges.repository.ChallengeJoinRepository;
import com.example.tracky.community.challenges.repository.ChallengeRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.friends.Friend;
import com.example.tracky.user.friends.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeInviteService {

    private final ChallengeInviteRepository challengeInviteRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeJoinRepository challengeJoinRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    /**
     * 챌린지 초대
     *
     * @param id
     * @param reqDTO
     * @param user
     * @return
     */
    @Transactional
    public List<ChallengeInviteResponse.saveDTO> challengesInvite(Integer id, ChallengeInviteRequest.InviteRequestDTO reqDTO, User user) {

        // 초대할 챌린지 조회
        Challenge challengePS = challengeRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.CHALLENGE_NOT_FOUND));

        List<ChallengeInviteResponse.saveDTO> saveDTO = new ArrayList<>();
        for (Integer frinedId : reqDTO.getFriendIds()) {
            User toUserPS = userRepository.findById(frinedId)
                    .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

            ChallengeInvite invite = ChallengeInvite.builder()
                    .fromUser(user)
                    .toUser(toUserPS)
                    .challenge(challengePS)
                    .status(InviteStatusEnum.PENDING)
                    .build();

            ChallengeInvite invitePS = challengeInviteRepository.save(invite);

            saveDTO.add(new ChallengeInviteResponse.saveDTO(invitePS));
        }

        return saveDTO;
    }

    /**
     * 챌린지 친구
     *
     * @param id
     * @param user
     * @return
     */
    public List<ChallengeInviteResponse.friendDTO> getFriend(Integer id, User user) {
        // 챌린지의 참여한 유저 조회
        List<User> joinUsersPS = challengeJoinRepository.findUserAllById(id);

        // 자신 조회
        User myUserPS = userRepository.findById(user.getId())
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 친구 조회
        List<Friend> friendsPS = friendRepository.findfriendByUserIdJoinFriend(user.getId());

        // 참여자 ID 목록 만들기 (검색 최적화용)
        List<Integer> joinedUserIds = new ArrayList<>();
        for (User u : joinUsersPS) {
            joinedUserIds.add(u.getId());
        }

        // DTO에 담을 배열 만들기
        List<ChallengeInviteResponse.friendDTO> friendDTO = new ArrayList<>();
        for (Friend friend : friendsPS) {
            ChallengeInviteResponse.friendDTO DTO = new ChallengeInviteResponse.friendDTO(friend, myUserPS);

            // 6. 챌린지 참여자가 아니면 추가
            if (!joinedUserIds.contains(DTO.getId())) {
                friendDTO.add(DTO);
            }
        }

        return friendDTO;
    }
}