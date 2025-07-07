package com.example.tracky.user.friends.friendinvite;

import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class FriendInviteController {
    private final FriendInviteService friendInviteService;

    @PostMapping("/friend-invite")
    public ResponseEntity<?> friendInvite(@RequestParam Integer toUserId) {
        // TODO: 로그인 유저로 교체
        User fromUser = User.builder().id(1).build();

        // 신청을 보내는 유저
        User toUser = User.builder().id(toUserId).build();

        FriendInviteRequest.SaveDTO response = friendInviteService.sendInvite(fromUser, toUser);
        return ResponseEntity.ok(response);
    }
}
