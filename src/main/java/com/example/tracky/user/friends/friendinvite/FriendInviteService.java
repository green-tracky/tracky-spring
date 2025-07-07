package com.example.tracky.user.friends.friendinvite;

import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FriendInviteService {
    private final FriendInviteRepository friendInviteRepository;

    @Transactional
    public FriendInviteRequest.SaveDTO sendInvite(User fromUser, User toUser) {
        FriendInvite invite = new FriendInvite(fromUser, toUser, LocalDateTime.now(), null, null); // 응답시간은 요청 받으면 넣어주기 / status 기본 값은 WAITING
        FriendInvite saved = friendInviteRepository.save(invite);
        return new FriendInviteRequest.SaveDTO(saved);
    }
}
