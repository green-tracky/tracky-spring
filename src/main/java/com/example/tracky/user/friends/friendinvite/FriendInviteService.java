package com.example.tracky.user.friends.friendinvite;

import com.example.tracky.user.User;
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

    @Transactional
    public FriendInviteRequest.SaveDTO sendInvite(User fromUser, User toUser) {
        FriendInvite invite = new FriendInvite(fromUser, toUser, LocalDateTime.now(), WAITING, null); // 응답시간은 요청 받으면 넣어주기 / status 기본 값은 WAITING
        FriendInvite saved = friendInviteRepository.save(invite);
        return new FriendInviteRequest.SaveDTO(saved);
    }

    public FriendInviteResponse.DTO findAll(User user) {
        List<FriendInvite> invites = friendInviteRepository.findAllByUserId(user.getId());
        List<FriendInviteResponse.InvitesDTO> inviteList = new ArrayList<>();
        for (FriendInvite invite : invites) {
            inviteList.add(new FriendInviteResponse.InvitesDTO(
                    invite.getId(),
                    invite.getFromUser(), // 요청 보낸 사람 정보
                    invite.getStatus(),
                    invite.getCreatedAt()
            ));
        }

        return new FriendInviteResponse.DTO(inviteList);
    }
}
