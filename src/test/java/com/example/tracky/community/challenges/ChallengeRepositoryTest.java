package com.example.tracky.community.challenges;

import com.example.tracky.community.challenges.repository.ChallengeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Slf4j
@Import(ChallengeRepository.class)
@DataJpaTest
public class ChallengeRepositoryTest {

    @Autowired
    private ChallengeRepository challengeRepository;

}
