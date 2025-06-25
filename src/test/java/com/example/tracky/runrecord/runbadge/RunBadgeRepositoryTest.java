package com.example.tracky.runrecord.runbadge;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class RunBadgeRepositoryTest {

    @Autowired
    private RunBadgeRepository runBadgeRepository;

    @Test
    void findAll_test() {
        List<RunBadge> runBadges = runBadgeRepository.findAll();
        for (RunBadge runBadge : runBadges) {
            log.debug("✅ 러닝뱃지 이름: " + runBadge.getName());
        }
    }

}

