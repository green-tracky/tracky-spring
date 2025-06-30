package com.example.tracky.community.challenge;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class ChallengeController {

    private final ChallengeService challengeService;

//    @GetMapping("/challenges")
//    public ResponseEntity<?> getChallenges() {
//        List<ChallengeResponse.DTO> respDTO = challengeService.getChallenges();
//        return Resp.ok(respDTO);
//    }

}
