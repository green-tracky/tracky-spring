package com.example.tracky.user.friends.friendinvite;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.enums.InviteStatusEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.user.User;
import com.example.tracky.user.friends.Friend;
import com.example.tracky.user.friends.FriendRepository;
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

    /**
     * 친구 요청 보내기
     *
     * @param fromUser 로그인 한 유저
     * @param toUser   친구 요청을 받을 유저
     * @return SaveDTO
     */
    @Transactional
    public FriendInviteResponse.SaveDTO friendInvite(User fromUser, User toUser) {
        // 본인에게 하는 요청 방지
        if (fromUser.getId().equals(toUser.getId())) {
            throw new ExceptionApi400(ErrorCodeEnum.INVALID_SELF_REQUEST);
        }

        // 중복 요청 방지
        if (friendInviteRepository.existsWaitingInvite(fromUser, toUser)) {
            throw new ExceptionApi400(ErrorCodeEnum.DUPLICATE_FRIEND_INVITE);
        }

        FriendInvite invite = FriendInvite.builder()
                .fromUser(fromUser)
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
     * @param user
     * @return DTO
     */
    public List<FriendInviteResponse.InvitesDTO> getFriendInvite(User user) {
        List<FriendInvite> invites = friendInviteRepository.findAllByUserId(user.getId());
        List<FriendInviteResponse.InvitesDTO> inviteList = new ArrayList<>();
        for (FriendInvite invite : invites) {
            inviteList.add(new FriendInviteResponse.InvitesDTO(invite));
        }

        return inviteList;
    }

    /**
     * 친구 수락
     *
     * @param inviteId 친구 요청 ID
     * @param user     로그인 한 유저
     * @return ResponseDTO
     */
    @Transactional
    public FriendInviteResponse.ResponseDTO friendInviteAccept(Integer inviteId, User user) {
        FriendInvite invite = friendInviteRepository.findValidateByInviteId(inviteId, user.getId())
                .orElseThrow(() -> new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED));

        // 권한 체크
        checkInviteRecipient(invite, user);

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
     * @param inviteId 친구 요청 ID
     * @param user     로그인 한 유저
     * @return ResponseDTO
     */
    @Transactional
    public FriendInviteResponse.ResponseDTO friendInviteReject(Integer inviteId, User user) {
        FriendInvite invite = friendInviteRepository.findValidateByInviteId(inviteId, user.getId())
                .orElseThrow(() -> new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED));

        // 권한 체크
        checkInviteRecipient(invite, user);

        // DB 상태 변경
        invite.reject();
        return new FriendInviteResponse.ResponseDTO(invite);
    }

    /**
     * 권한 체크
     *
     * @param invite
     * @param user
     */
    private void checkInviteRecipient(FriendInvite invite, User user) {
        if (!invite.getToUser().getId().equals(user.getId())) {
            throw new ExceptionApi404(ErrorCodeEnum.ACCESS_DENIED);
        }
    }

}
