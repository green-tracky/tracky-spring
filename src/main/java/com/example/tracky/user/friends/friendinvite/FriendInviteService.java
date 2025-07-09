package com.example.tracky.user.friends.friendinvite;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.example.tracky.user.User;
import com.example.tracky.user.friends.Friend;
import com.example.tracky.user.friends.FriendRepository;
import com.example.tracky.user.friends.friendinvite.utils.FriendInviteUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.tracky.user.friends.friendinvite.enums.InviteStatusType.WAITING;

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
    public FriendInviteRequest.SaveDTO sendInvite(User fromUser, User toUser) {
        if (fromUser.getId().equals(toUser.getId())) {
            throw new ExceptionApi400(ErrorCodeEnum.INVALID_SELF_REQUEST);
        }

        if (friendInviteRepository.existsWaitingInvite(fromUser, toUser)) {
            throw new ExceptionApi400(ErrorCodeEnum.DUPLICATE_FRIEND_INVITE);
        }

        FriendInvite invite = FriendInvite.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .createdAt(LocalDateTime.now())
                .status(WAITING)
                .build(); // 응답시간은 요청 받으면 넣어주기 / status 기본 값은 WAITING

        FriendInvite savePS = friendInviteRepository.save(invite);
        return new FriendInviteRequest.SaveDTO(savePS);
    }

    /**
     * 내가 받은 요청 모두 조회
     *
     * @param user
     * @return DTO
     */
    public FriendInviteResponse.DTO findAll(User user) {
        List<FriendInvite> invites = friendInviteRepository.findAllByUserId(user.getId());
        List<FriendInviteResponse.InvitesDTO> inviteList = new ArrayList<>();
        for (FriendInvite invite : invites) {
            inviteList.add(new FriendInviteResponse.InvitesDTO(invite));
        }

        return new FriendInviteResponse.DTO(inviteList);
    }

    /**
     * 친구 수락
     *
     * @param inviteId 친구 요청 ID
     * @param user     로그인 한 유저
     * @return ResponseDTO
     */
    @Transactional
    public FriendInviteResponse.ResponseDTO acceptInvite(Integer inviteId, User user) {
        FriendInvite invite = friendInviteRepository.findValidateByInviteId(inviteId, user.getId());

        // 권한 체크
        FriendInviteUtil.FriendInviteResult(invite, user);

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
    public FriendInviteResponse.ResponseDTO rejectInvite(Integer inviteId, User user) {
        FriendInvite invite = friendInviteRepository.findValidateByInviteId(inviteId, user.getId());
        
        // 권한 체크
        FriendInviteUtil.FriendInviteResult(invite, user);

        // DB 상태 변경
        invite.reject();
        return new FriendInviteResponse.ResponseDTO(invite);
    }
}
