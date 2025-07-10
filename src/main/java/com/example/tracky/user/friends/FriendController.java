package com.example.tracky.user.friends;

import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class FriendController {
    private final FriendService friendService;

    @GetMapping("/friend/search")
    public ResponseEntity<?> getFriendSearch(@RequestParam("user-tag") String userTag) {
        // TODO: 로그인 유저로 교체
        User user = User.builder().id(1).build();

        List<FriendResponse.SearchDTO> respDTO = friendService.getFriendSearch(userTag, user);
        return ResponseEntity.ok(respDTO);
    }

}
