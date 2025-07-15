package com.example.tracky.user.friends;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated // 1. 이 컨트롤러에서 유효성 검사를 활성화합니다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class FriendController {
    private final FriendService friendService;
    private final HttpSession session;

    @GetMapping("/friends/search")
    public ResponseEntity<?> getFriendSearch(@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "유저 태그는 영문, 숫자만 사용할 수 있습니다.") @RequestParam("user-tag") String userTag) {
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);
        
        List<FriendResponse.SearchDTO> respDTO = friendService.getFriendSearch(userTag, sessionProfile);
        return Resp.ok(respDTO);
    }

    @GetMapping("/friends/list")
    public ResponseEntity<?> getFriendList() {
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        List<FriendResponse.UserDTO> respDTO = friendService.getFriendList(sessionProfile);

        return Resp.ok(respDTO);
    }
}
