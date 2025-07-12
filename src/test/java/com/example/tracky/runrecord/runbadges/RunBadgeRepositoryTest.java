package com.example.tracky.runrecord.runbadges;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@Slf4j
@Import({RunBadgeRepository.class, RunBadgeRepository.class})
@DataJpaTest
public class RunBadgeRepositoryTest {

    @Autowired
    private RunBadgeRepository runBadgeRepository;

    @Test
    void findAll_test() {
        List<RunBadge> runBadgesPS = runBadgeRepository.findAll();
        for (RunBadge runBadge : runBadgesPS) {
            log.debug("✅ 러닝뱃지 이름: " + runBadge.getName());
        }
    }

}

