package com.example.tracky.user.friends.friendinvite;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class FriendInviteController {
    private final FriendInviteService friendInviteService;
    private final HttpSession session;

    @PostMapping("/friends/invite/users/{toUserId}")
    public ResponseEntity<?> friendInvite(@PathVariable("toUserId") Integer toUserId) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        FriendInviteResponse.SaveDTO respDTO = friendInviteService.friendInvite(sessionProfile, toUserId);
        return Resp.ok(respDTO);
    }

    @PutMapping("/friends/invite/{id}/accept")
    public ResponseEntity<?> friendInviteAccept(@PathVariable("id") Integer inviteId) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        FriendInviteResponse.ResponseDTO respDTO = friendInviteService.friendInviteAccept(inviteId, sessionProfile);
        return Resp.ok(respDTO);
    }

    @PutMapping("/friends/invite/{id}/reject")
    public ResponseEntity<?> friendInviteReject(@PathVariable("id") Integer inviteId) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        FriendInviteResponse.ResponseDTO respDTO = friendInviteService.friendInviteReject(inviteId, sessionProfile);
        return Resp.ok(respDTO);
    }
}
