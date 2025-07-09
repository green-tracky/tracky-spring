package com.example.tracky.user;

import com.example.tracky._core.utils.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/oauth/login")
    public ResponseEntity<?> login(@RequestBody String idToken) {
        userService.카카오로그인(idToken);
        return Resp.ok(idToken);
    }

}
