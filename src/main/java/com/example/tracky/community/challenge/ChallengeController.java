package com.example.tracky.community.challenge;

import java.util.List;

import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import com.example.tracky.runrecord.runbadge.RunBadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping("/challenges")
    public ResponseEntity<?> getChallenges() {
        List<ChallengeResponse.DTO> respDTO = challengeService.getChallenges();
        return Resp.ok(respDTO);
    }

}
