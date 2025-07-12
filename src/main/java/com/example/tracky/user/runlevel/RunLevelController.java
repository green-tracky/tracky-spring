package com.example.tracky.user.runlevel;

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
public class RunLevelController {

    private final RunLevelService runLevelService;
    private final HttpSession session;

    @GetMapping("/run-levels")
    public ResponseEntity<?> getRunLevels() {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        RunLevelResponse.ListDTO respDTO = runLevelService.getRunLevels(sessionProfile);
        return Resp.ok(respDTO);
    }

}
