package com.example.tracky.community.challenge;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public List<ChallengeResponse.DTO> getChallenges() {
        List<Challenge> challenges = challengeRepository.findAll();

        return challenges.stream()
                .map(challenge -> new ChallengeResponse.DTO(challenge))
                .toList();
    }
 
}