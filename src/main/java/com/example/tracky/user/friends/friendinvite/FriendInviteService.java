package com.example.tracky.user.friends.friendinvite;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.enums.InviteStatusEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendInviteService {
    private final FriendInviteRepository friendInviteRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


    /**
     * 친구 요청 하기
     *
     * @param sessionProfile
     * @param userId
     * @return
     */
    @Transactional
    public FriendInviteResponse.SaveDTO friendInvite(OAuthProfile sessionProfile, Integer userId) {

        // 1. 사용자 조회
        User fromUserPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 2. 초대받을 대상 조회
        User toUserPS = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 3. 본인에게 하는 요청 방지
        if (fromUserPS.getId().equals(toUserPS.getId())) {
            throw new ExceptionApi400(ErrorCodeEnum.INVALID_SELF_REQUEST);
        }

        // 4. 중복 요청 방지
        if (friendInviteRepository.existsWaitingInvite(fromUserPS, toUserPS)) {
            throw new ExceptionApi400(ErrorCodeEnum.DUPLICATE_INVITE);
        }

        // 5. DB에 친구 요청 저장
        FriendInvite invite = FriendInvite.builder()
                .fromUser(fromUserPS)
                .toUser(toUserPS)
                .status(InviteStatusEnum.PENDING)
                .build();
        FriendInvite saveInvitePS = friendInviteRepository.save(invite);

        // 6. FCM을 통해 알림 전송 (비동기 처리 권장)
        // NotificationService에 알림 전송을 위임합니다.
        // *실제 서비스에서는 이 부분을 @Async 어노테이션 등을 사용하여 비동기적으로 처리하는 것이 좋습니다.
        //  그래야 알림 전송이 지연되더라도 사용자에게 응답이 늦게 가는 것을 막을 수 있습니다.
        notificationService.sendFriendInviteNotification(fromUserPS.getId(), toUserPS.getId());

        log.info("{}({})이 {}({})에게 친구 요청을 보냈습니다", fromUserPS.getUsername(), fromUserPS.getId(), toUserPS.getUsername(), toUserPS.getId());

        return new FriendInviteResponse.SaveDTO(saveInvitePS);
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
            friendRepository.save(Friend.builder().fromUser(invite.getFromUser()).toUser(invite.getToUser()).build());
        }

        log.info("{}({})이 친구 요청을 수락했습니다", userPS.getUsername(), userPS.getId());

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

        log.info("{}({})이 친구 요청을 거절했습니다", userPS.getUsername(), userPS.getId());


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
