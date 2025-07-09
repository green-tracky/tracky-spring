package com.example.tracky.user.friends.friendinvite;

import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class FriendInviteController {
    private final FriendInviteService friendInviteService;

    @GetMapping("/friend-invite")
    public ResponseEntity<?> getFriendInvite() {
        User user = User.builder().id(1).build();

        FriendInviteResponse.DTO respDTO = friendInviteService.findAll(user);
        return ResponseEntity.ok(respDTO);
    }

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
