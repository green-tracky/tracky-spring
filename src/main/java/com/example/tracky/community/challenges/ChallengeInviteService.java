package com.example.tracky.community.challenges;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.enums.InviteStatusEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.domain.ChallengeInvite;
import com.example.tracky.community.challenges.dto.ChallengeInviteRequest;
import com.example.tracky.community.challenges.dto.ChallengeInviteResponse;
import com.example.tracky.community.challenges.repository.ChallengeInviteRepository;
import com.example.tracky.community.challenges.repository.ChallengeJoinRepository;
import com.example.tracky.community.challenges.repository.ChallengeRepository;
import com.example.tracky.notification.NotificationService;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.friends.Friend;
import com.example.tracky.user.friends.FriendRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeInviteService {

    private final ChallengeInviteRepository challengeInviteRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeJoinRepository challengeJoinRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final NotificationService notificationService;

    /**
     * 챌린지 초대
     *
     * @param id
     * @param reqDTO
     * @param sessionProfile
     * @return
     */
    @Transactional
    public List<ChallengeInviteResponse.saveDTO> challengesInvite(Integer id, ChallengeInviteRequest.InviteRequestDTO reqDTO, OAuthProfile sessionProfile) {
        // 사용자 조회
        User fromUserPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));


        // 초대할 챌린지 조회
        Challenge challengePS = challengeRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.CHALLENGE_NOT_FOUND));

        // 2. 본인이 해당 챌린지에 참여했는지 확인
        boolean isParticipating = challengeJoinRepository.existsByUserIdAndChallengeId(fromUserPS.getId(), id);
        if (!isParticipating) {
            throw new ExceptionApi404(ErrorCodeEnum.CHALLENGE_JOIN_NOT_FOUND); // 참여하지 않은 챌린지엔 초대 불가
        }

        List<Friend> myFriends = friendRepository.findfriendByUserIdJoinFriend(fromUserPS.getId());
        Set<Integer> myFriendIds = new HashSet<>();
        for (Friend f : myFriends) {
            Integer fromId = f.getFromUser().getId();
            Integer toId = f.getToUser().getId();

            // 내가 fromUser면 친구는 toUser고, 반대면 친구는 fromUser
            if (fromId.equals(fromUserPS.getId())) {
                myFriendIds.add(toId);
            } else {
                myFriendIds.add(fromId);
            }
        }

        List<ChallengeInviteResponse.saveDTO> saveDTO = new ArrayList<>();

        for (Integer friendId : reqDTO.getFriendIds()) {
            // 내 친구가 아닌 사람은 초대 불가
            if (!myFriendIds.contains(friendId)) {
                throw new ExceptionApi404(ErrorCodeEnum.NOT_MY_FRIEND);
            }
            User toUserPS = userRepository.findById(friendId)
                    .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

            // 3-2. 이미 초대한 기록 있는지 확인 (중복 방지)
            boolean alreadyInvited = challengeInviteRepository.existsByFromUserIdAndToUserIdAndChallengeId(
                    fromUserPS.getId(), toUserPS.getId(), challengePS.getId());
            if (alreadyInvited) {
                throw new ExceptionApi404(ErrorCodeEnum.DUPLICATE_INVITE); // 참여하지 않은 챌린지엔 초대 불가
            }
            ChallengeInvite invite = ChallengeInvite.builder()
                    .fromUser(fromUserPS)
                    .toUser(toUserPS)
                    .challenge(challengePS)
                    .status(InviteStatusEnum.PENDING)
                    .build();

            ChallengeInvite invitePS = challengeInviteRepository.save(invite);
            notificationService.sendChallengeInviteNotification(fromUserPS.getId(), toUserPS.getId(), challengePS.getId());

            saveDTO.add(new ChallengeInviteResponse.saveDTO(invitePS));
        }

        log.info("{}({})이 {}에게 챌린지 {}({}) 초대를 보냈습니다", fromUserPS.getUsername(), fromUserPS.getId(), myFriendIds, challengePS.getName(), challengePS.getId());

        return saveDTO;
    }

    /**
     * 챌린지 친구
     *
     * @param id
     * @param sessionProfile
     * @return
     */
    public List<ChallengeInviteResponse.friendDTO> getAvailableFriends(Integer id, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        if (!challengeJoinRepository.existsByUserIdAndChallengeId(userPS.getId(), id)) {
            throw new ExceptionApi404(ErrorCodeEnum.CHALLENGE_JOIN_NOT_FOUND);
        }

        // 챌린지의 참여한 유저 조회
        List<User> joinUsersPS = challengeJoinRepository.findUserAllById(id);

        // 친구 조회
        List<Friend> friendsPS = friendRepository.findfriendByUserIdJoinFriend(userPS.getId());

        // 참여자 ID 목록 만들기 (검색 최적화용)
        List<Integer> joinedUserIds = new ArrayList<>();
        for (User u : joinUsersPS) {
            joinedUserIds.add(u.getId());
        }

        // DTO에 담을 배열 만들기
        List<ChallengeInviteResponse.friendDTO> friendDTO = new ArrayList<>();
        for (Friend friend : friendsPS) {
            ChallengeInviteResponse.friendDTO DTO = new ChallengeInviteResponse.friendDTO(friend, userPS);

            // 6. 챌린지 참여자가 아니면 추가
            if (!joinedUserIds.contains(DTO.getId())) {
                friendDTO.add(DTO);
            }
        }
        log.info("{}({})이 초대가능한 친구목록을 조회했습니다. ", userPS.getUsername(), userPS.getId());

        return friendDTO;
    }

    /**
     * 챌린지 수락
     *
     * @param inviteId       친구 요청 ID
     * @param sessionProfile 로그인 한 유저
     * @return ResponseDTO
     */
    @Transactional
    public ChallengeInviteResponse.ResponseDTO challengesInviteAccept(Integer inviteId, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 나의 초대 인지 확인
        ChallengeInvite invite = challengeInviteRepository.findValidateByInviteId(inviteId, userPS.getId())
                .orElseThrow(() -> new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED));

        // 권한 체크
        checkInviteRecipient(invite, userPS);

        // DB 상태 변경
        invite.accept();

        // 친구 테이블에 추가 (중복 방지)
        if (!friendRepository.existsFriend(invite.getFromUser(), invite.getToUser())) {
            friendRepository.save(new Friend(invite.getFromUser(), invite.getToUser()));
        }

        log.info("{}({})이 챌린지 초대를 수락하였습니다", userPS.getUsername(), userPS.getId());

        return new ChallengeInviteResponse.ResponseDTO(invite);
    }

    /**
     * 챌린지 거절
     *
     * @param inviteId       친구 요청 ID
     * @param sessionProfile 로그인 한 유저
     * @return ResponseDTO
     */
    @Transactional
    public ChallengeInviteResponse.ResponseDTO challengesInviteReject(Integer inviteId, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        ChallengeInvite invite = challengeInviteRepository.findValidateByInviteId(inviteId, userPS.getId())
                .orElseThrow(() -> new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED));

        // 권한 체크
        checkInviteRecipient(invite, userPS);

        // DB 상태 변경
        invite.reject();

        log.info("{}({})이 챌린지 초대를 거절했습니다. ", userPS.getUsername(), userPS.getId());

        return new ChallengeInviteResponse.ResponseDTO(invite);
    }

    /**
     * 권한 체크
     *
     * @param invite
     * @param user
     */
    private void checkInviteRecipient(ChallengeInvite invite, User user) {

        if (!invite.getToUser().getId().equals(user.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);

        }
    }
}