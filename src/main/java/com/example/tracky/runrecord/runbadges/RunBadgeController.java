package com.example.tracky.runrecord.runbadges;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class RunBadgeController {

    private final RunBadgeService runBadgeService;
    private final HttpSession session;

    @GetMapping("/run-badges")
    public ResponseEntity<?> getRunBadges() {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        RunBadgeResponse.GroupedBadgeListDTO respDTO = runBadgeService.getRunBadges(sessionProfile);
        return Resp.ok(respDTO);
    }

}