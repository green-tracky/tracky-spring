package com.example.tracky.community.challenges;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.utils.Resp;
import com.example.tracky.community.challenges.dto.ChallengeJoinResponse;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class ChallengeJoinController {
    private final ChallengeJoinService challengeJoinService;
    private final HttpSession session;

    @PostMapping("/community/challenges/{id}/join")
    public ResponseEntity<?> join(@PathVariable Integer id) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        ChallengeJoinResponse.DTO respDTO = challengeJoinService.join(id, sessionProfile);
        return Resp.ok(respDTO);
    }


    @DeleteMapping("/community/challenges/{id}/join")
    public ResponseEntity<?> leave(@PathVariable Integer id) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        challengeJoinService.leave(id, sessionProfile);
        return Resp.ok(null);
    }

}
