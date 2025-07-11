package com.example.tracky.user.friends.friendinvite;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.enums.InviteStatusEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.friends.Friend;
import com.example.tracky.user.friends.FriendRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FriendInviteService {
    private final FriendInviteRepository friendInviteRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    /**
     * 친구 요청 보내기
     *
     * @param sessionProfile 로그인 한 유저
     * @param toUser         친구 요청을 받을 유저
     * @return SaveDTO
     */
    @Transactional
    public FriendInviteResponse.SaveDTO friendInvite(OAuthProfile sessionProfile, User toUser) {

        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 본인에게 하는 요청 방지
        if (userPS.getId().equals(toUser.getId())) {
            throw new ExceptionApi400(ErrorCodeEnum.INVALID_SELF_REQUEST);
        }

        // 중복 요청 방지
        if (friendInviteRepository.existsWaitingInvite(userPS, toUser)) {
            throw new ExceptionApi400(ErrorCodeEnum.DUPLICATE_INVITE);
        }

        FriendInvite invite = FriendInvite.builder()
                .fromUser(userPS)
                .toUser(toUser)
                .createdAt(LocalDateTime.now())
                .status(InviteStatusEnum.PENDING)
                .build(); // 응답시간은 요청 받으면 넣어주기 / status 기본 값은 WAITING

        FriendInvite savePS = friendInviteRepository.save(invite);

        return new FriendInviteResponse.SaveDTO(savePS);
    }

    /**
     * 내가 받은 친구 요청 모두 조회
     *
     * @param sessionProfile
     * @return DTO
     */
    public List<FriendInviteResponse.InvitesDTO> getFriendInvite(OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        List<FriendInvite> invites = friendInviteRepository.findAllByUserId(userPS.getId());
        List<FriendInviteResponse.InvitesDTO> inviteList = new ArrayList<>();
        for (FriendInvite invite : invites) {
            inviteList.add(new FriendInviteResponse.InvitesDTO(invite));
        }

        return inviteList;
    }

    /**
     * 친구 수락
     *
     * @param inviteId       친구 요청 ID
     * @param sessionProfile 로그인 한 유저
     * @return ResponseDTO
     */
    @Transactional
    public FriendInviteResponse.ResponseDTO friendInviteAccept(Integer inviteId, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        FriendInvite invite = friendInviteRepository.findValidateByInviteId(inviteId, userPS.getId())
                .orElseThrow(() -> new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED));

        // 권한 체크
        checkInviteRecipient(invite, userPS);

        // DB 상태 변경
        invite.accept();

        // 친구 테이블에 추가 (중복 방지)
        if (!friendRepository.existsFriend(invite.getFromUser(), invite.getToUser())) {
            friendRepository.save(new Friend(invite.getFromUser(), invite.getToUser()));
        }

        return new FriendInviteResponse.ResponseDTO(invite);
    }

    /**
     * 친구 거절
     *
     * @param inviteId       친구 요청 ID
     * @param sessionProfile 로그인 한 유저
     * @return ResponseDTO
     */
    @Transactional
    public FriendInviteResponse.ResponseDTO friendInviteReject(Integer inviteId, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        FriendInvite invite = friendInviteRepository.findValidateByInviteId(inviteId, userPS.getId())
                .orElseThrow(() -> new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED));

        // 권한 체크
        checkInviteRecipient(invite, userPS);

        // DB 상태 변경
        invite.reject();
        return new FriendInviteResponse.ResponseDTO(invite);
    }

    /**
     * 권한 체크
     *
     * @param invite
     * @param userPS
     */
    private void checkInviteRecipient(FriendInvite invite, User userPS) {

        if (!invite.getToUser().getId().equals(userPS.getId())) {
            throw new ExceptionApi404(ErrorCodeEnum.ACCESS_DENIED);
        }
    }

}
