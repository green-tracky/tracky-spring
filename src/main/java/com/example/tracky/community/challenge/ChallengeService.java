package com.example.tracky.community.challenge;

import com.example.tracky.community.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

//    public List<ChallengeResponse.DTO> getChallenges() {
//        List<Challenge> challenges = challengeRepository.findAll();
//
//        return challenges.stream()
//                .map(challenge -> new ChallengeResponse.DTO(challenge))
//                .toList();
//    }

}