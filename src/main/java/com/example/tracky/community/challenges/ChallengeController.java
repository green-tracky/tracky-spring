package com.example.tracky.community.challenges;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.utils.Resp;
import com.example.tracky.community.challenges.dto.ChallengeRequest;
import com.example.tracky.community.challenges.dto.ChallengeResponse;
import com.example.tracky.community.leaderboard.LeaderBoardService;
import com.example.tracky.community.leaderboard.LeaderBoardsResponse;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final LeaderBoardService leaderBoardService;
    private final HttpSession session;

    @GetMapping("/community/challenges")
    public ResponseEntity<?> getChallenges() {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        ChallengeResponse.MainDTO respDTO = challengeService.getChallenges(sessionProfile);
        return Resp.ok(respDTO);
    }

    @GetMapping("/community/challenges/{id}")
    public ResponseEntity<?> getChallenge(@PathVariable("id") Integer id) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        ChallengeResponse.DetailDTO respDTO = challengeService.getChallenge(id, sessionProfile);
        return Resp.ok(respDTO);
    }

    @GetMapping("/community/challenges/{id}/leaderboard")
    public ResponseEntity<?> getChallengeLeaderBoard(@PathVariable("id") Integer id) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        LeaderBoardsResponse.ChallengeLeaderBoardDTO respDTO = leaderBoardService.getChallengeLeaderBoards(id, sessionProfile);
        return Resp.ok(respDTO);
    }

    @PostMapping("/community/challenges")
    public ResponseEntity<?> save(@Valid @RequestBody ChallengeRequest.SaveDTO reqDTO, Errors errors) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        ChallengeResponse.SaveDTO respDTO = challengeService.save(sessionProfile, reqDTO);

        return Resp.ok(respDTO);
    }

    @PutMapping("/community/challenges/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id, @Valid @RequestBody ChallengeRequest.UpdateDTO reqDTO, Errors errors) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        ChallengeResponse.UpdateDTO respDTO = challengeService.update(id, sessionProfile, reqDTO);

        return Resp.ok(respDTO);
    }
}
