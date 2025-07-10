package com.example.tracky.community.challenges;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.community.challenges.dto.ChallengeInviteResponse;
import com.example.tracky.community.leaderboard.LeaderBoardService;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class ChallengeInviteController {

    private final ChallengeInviteService challengeService;
    private final LeaderBoardService leaderBoardService;

    @PostMapping("/community/challenges/{id}/invite/{userId}")
    public ResponseEntity<?> challengesInvite(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        Integer myuserId = 1;
        User user = User.builder().id(myuserId).build();

        ChallengeInviteResponse.saveDTO respDTO = challengeService.challengesInvite(id, userId, user);
        return Resp.ok(respDTO);
    }
}
