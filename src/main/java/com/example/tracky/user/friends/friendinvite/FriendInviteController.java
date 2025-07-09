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
        // TODO: 로그인 유저로 교체
        User user = User.builder().id(1).build();

        FriendInviteResponse.DTO respDTO = friendInviteService.getFriendInvite(user);
        return ResponseEntity.ok(respDTO);
    }

    @PostMapping("/friend-invite")
    public ResponseEntity<?> friendInvite(@RequestParam Integer toUserId) {
        // TODO: 로그인 유저로 교체
        User fromUser = User.builder().id(1).build();

        // 신청을 보내는 유저
        User toUser = User.builder().id(toUserId).build();

        FriendInviteRequest.SaveDTO response = friendInviteService.friendInvite(fromUser, toUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/friend-invite/{id}/accept")
    public ResponseEntity<?> friendInviteAccept(@PathVariable("id") Integer inviteId) {
        // TODO: 로그인 유저로 교체
        User user = User.builder().id(1).build();

        FriendInviteResponse.ResponseDTO response = friendInviteService.friendInviteAccept(inviteId, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/friend-invite/{id}/reject")
    public ResponseEntity<?> friendInviteReject(@PathVariable("id") Integer inviteId) {
        // TODO: 로그인 유저로 교체
        User user = User.builder().id(1).build();


        FriendInviteResponse.ResponseDTO response = friendInviteService.friendInviteReject(inviteId, user);
        return ResponseEntity.ok(response);
    }
}
