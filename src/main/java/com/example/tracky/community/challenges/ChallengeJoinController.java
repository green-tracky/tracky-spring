package com.example.tracky.community.challenges;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.community.challenges.dto.ChallengeJoinResponse;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class ChallengeJoinController {
    private final ChallengeJoinService challengeJoinService;

    @PostMapping("/community/challenges/{id}/join")
    public ResponseEntity<?> join(@PathVariable Integer id) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        ChallengeJoinResponse.DTO respDTO = challengeJoinService.join(id, user);
        return Resp.ok(respDTO);
    }


    @DeleteMapping("/community/challenges/{id}/join")
    public ResponseEntity<?> leave(@PathVariable Integer id) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        challengeJoinService.leave(id, user);
        return Resp.ok(null);
    }

}
