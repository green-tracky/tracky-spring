package com.example.tracky.community.challenges;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.community.challenges.dto.ChallengeInviteRequest;
import com.example.tracky.community.challenges.dto.ChallengeInviteResponse;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class ChallengeInviteController {

    private final ChallengeInviteService challengeInviteService;

    @PostMapping("/community/challenges/{id}/invite")
    public ResponseEntity<?> challengesInvite(@PathVariable("id") Integer id, @RequestBody ChallengeInviteRequest.InviteRequestDTO reqDTO) {
        User user = User.builder().id(6).build();

        List<ChallengeInviteResponse.saveDTO> respDTO = challengeInviteService.challengesInvite(id, reqDTO, user);
        return Resp.ok(respDTO);
    }

    @GetMapping("/community/challenges/{id}/friend")
    public ResponseEntity<?> getFriend(@PathVariable("id") Integer id) {
        User user = User.builder().id(1).build();

        List<ChallengeInviteResponse.friendDTO> respDTO = challengeInviteService.getFriend(id, user);
        return Resp.ok(respDTO);
    }

    @PostMapping("/community/challenges/{id}/accept")
    public ResponseEntity<?> friendInviteAccept(@PathVariable("id") Integer inviteId) {
        // TODO: 로그인 유저로 교체
        User user = User.builder().id(6).build();

        ChallengeInviteResponse.ResponseDTO respDTO = challengeInviteService.challengesInviteAccept(inviteId, user);
        return ResponseEntity.ok(respDTO);
    }

    @PostMapping("/community/challenges/{id}/reject")
    public ResponseEntity<?> friendInviteReject(@PathVariable("id") Integer inviteId) {
        // TODO: 로그인 유저로 교체
        User user = User.builder().id(6).build();

        ChallengeInviteResponse.ResponseDTO respDTO = challengeInviteService.challengesInviteReject(inviteId, user);
        return ResponseEntity.ok(respDTO);
    }
}
