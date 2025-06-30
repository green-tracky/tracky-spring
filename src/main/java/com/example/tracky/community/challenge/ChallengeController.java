package com.example.tracky.community.challenge;

import com.example.tracky._core.utils.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping("/challenges")
    public ResponseEntity<?> getChallenges() {
        var respDTO = challengeService.getChallenges();
        return Resp.ok(respDTO);
    }

}
