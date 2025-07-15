package com.example.tracky.community.challenges;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.utils.Resp;
import com.example.tracky.community.challenges.dto.ChallengeInviteRequest;
import com.example.tracky.community.challenges.dto.ChallengeInviteResponse;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class ChallengeInviteController {

    private final ChallengeInviteService challengeInviteService;
    private final HttpSession session;


    @PostMapping("/community/challenges/{id}/invite")
    public ResponseEntity<?> challengesInvite(@PathVariable("id") Integer id, @Valid @RequestBody ChallengeInviteRequest.InviteRequestDTO reqDTO, Errors errors) {
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        List<ChallengeInviteResponse.saveDTO> respDTO = challengeInviteService.challengesInvite(id, reqDTO, sessionProfile);
        return Resp.ok(respDTO);
    }

    @GetMapping("/community/challenges/{id}/invite/available-friends")
    public ResponseEntity<?> getAvailableFriends(@PathVariable("id") Integer id) {
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        List<ChallengeInviteResponse.friendDTO> respDTO = challengeInviteService.getAvailableFriends(id, sessionProfile);
        return Resp.ok(respDTO);
    }

    @PutMapping("/community/challenges/invite/{id}/accept")
    public ResponseEntity<?> friendInviteAccept(@PathVariable("id") Integer inviteId) {
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        ChallengeInviteResponse.ResponseDTO respDTO = challengeInviteService.challengesInviteAccept(inviteId, sessionProfile);
        return Resp.ok(respDTO);
    }

    @PutMapping("/community/challenges/invite/{id}/reject")
    public ResponseEntity<?> friendInviteReject(@PathVariable("id") Integer inviteId) {
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        ChallengeInviteResponse.ResponseDTO respDTO = challengeInviteService.challengesInviteReject(inviteId, sessionProfile);
        return Resp.ok(respDTO);
    }
}
