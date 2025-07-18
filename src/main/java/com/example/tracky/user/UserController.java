package com.example.tracky.user;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final HttpSession session;

    @PostMapping("/api/oauth/kakao/login")
    public ResponseEntity<?> kakaoLogin(@Valid @RequestBody UserRequest.IdTokenDTO reqDTO, Errors errors) {

        UserResponse.IdTokenDTO respDTO = userService.kakaoLogin(reqDTO);
        return Resp.ok(respDTO);
    }

    @PutMapping("/s/api/users/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id, @Valid @RequestBody UserRequest.UpdateDTO reqDTO, Errors errors) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        UserResponse.UpdateDTO respDTO = userService.update(id, reqDTO, sessionProfile);
        return Resp.ok(respDTO);
    }

    @DeleteMapping("/s/api/users/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        userService.delete(id, sessionProfile);
        return Resp.ok(null);
    }

    @GetMapping("/s/api/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Integer id) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        UserResponse.DetailDTO respDTO = userService.getUser(id, sessionProfile);
        return Resp.ok(respDTO);
    }

    @PutMapping("/s/api/users/{id}/fcm-token")
    public ResponseEntity<?> updateFCMToken(@PathVariable("id") Integer id, @Valid @RequestBody UserRequest.FCMDTO reqDTO, Errors errors) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        userService.updateFCMToken(id, sessionProfile, reqDTO);
        return Resp.ok(null);
    }

}
