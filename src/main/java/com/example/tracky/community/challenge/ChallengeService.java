package com.example.tracky.community.challenge;

import java.util.List;
import java.util.stream.Collectors;

import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.runrecord.runbadge.RunBadgeRepository;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import org.springframework.stereotype.Service;

import com.example.tracky.runrecord.runbadge.RunBadgeResponse.DTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    @Transactional
    public List<ChallengeResponse.DTO> getChallenges() {
        List<Challenge> challenges = challengeRepository.findAll();

        return challenges.stream()
                .map(challenge -> new ChallengeResponse.DTO(challenge))
                .toList();
    }

}