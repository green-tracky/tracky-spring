package com.example.tracky.community.challenges;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.community.challenges.dto.ChallengeResponse;
import com.example.tracky.community.leaderboard.LeaderBoardService;
import com.example.tracky.community.leaderboard.LeaderBoardsResponse;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final LeaderBoardService leaderBoardService;

    @GetMapping("/community/challenges")
    public ResponseEntity<?> getChallenges() {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();
        ChallengeResponse.MainDTO respDTO = challengeService.getChallenges(user);
        return Resp.ok(respDTO);
    }

    @GetMapping("/community/challenges/{id}")
    public ResponseEntity<?> getChallenge(@PathVariable Integer id) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        ChallengeResponse.DetailDTO respDTO = challengeService.getChallenge(id, user);
        return Resp.ok(respDTO);
    }

    @GetMapping("/community/challenges/{id}/leaderboard")
    public ResponseEntity<?> getChallengeLeaderBoard(@PathVariable Integer id) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        LeaderBoardsResponse.ChallengeLeaderBoardDTO respDTO = leaderBoardService.getChallengeLederBoards(id, user);
        return Resp.ok(respDTO);
    }


}
