package com.example.tracky.user;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final HttpSession session;

    @PostMapping("/api/oauth/kakao/login")
    public ResponseEntity<?> kakaoLogin(@RequestBody String idToken) {
        UserResponse.IdTokenDTO respDTO = userService.kakaoLogin(idToken);
        return Resp.ok(respDTO);
    }

    @PutMapping("/s/api/user/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody UserRequest.UpdateDTO reqDTO) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        UserResponse.UpdateDTO respDTO = userService.update(id, reqDTO, sessionProfile);
        return Resp.ok(respDTO);
    }

    @DeleteMapping("/s/api/user/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        userService.delete(id, sessionProfile);
        return Resp.ok(null);
    }

    @GetMapping("/s/api/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable Integer id) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        UserResponse.DetailDTO respDTO = userService.getUser(id, sessionProfile);
        return Resp.ok(respDTO);
    }

}
