package com.example.tracky.community.challenge;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.runrecord.runbadge.RunBadgeRepository;

@SpringBootTest
public class ChallengeRepositoryTest {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Test
    void findAll_test() {
        List<Challenge> challenges = challengeRepository.findAll();
        for (Challenge challenge : challenges) {
            System.out.println(challenge.getName());
        }
    }

}
