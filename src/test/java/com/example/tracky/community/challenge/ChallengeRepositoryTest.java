package com.example.tracky.community.challenge;

import com.example.tracky.community.challenge.domain.Challenge;
import com.example.tracky.community.challenge.repository.ChallengeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@Slf4j
@Import({ChallengeRepository.class, ChallengeRepository.class})
@DataJpaTest
public class ChallengeRepositoryTest {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Test
    void findAll_test() {
        List<Challenge> challenges = challengeRepository.findAll();
        for (Challenge challenge : challenges) {
            log.debug("✅ 챌린지 이름: " + challenge.getName());
        }
    }

}
