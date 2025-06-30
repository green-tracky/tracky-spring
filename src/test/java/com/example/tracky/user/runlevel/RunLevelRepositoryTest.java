package com.example.tracky.user.runlevel;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@Slf4j
@Import(RunLevelRepository.class)
@DataJpaTest
public class RunLevelRepositoryTest {

    @Autowired
    private RunLevelRepository runLevelRepository;

    @Test
    void findAll_test() {
        List<RunLevel> runLevels = runLevelRepository.findAll();
        for (RunLevel runLevel : runLevels) {
            log.debug("✅ 런레벨 이름: " + runLevel.getName());
        }
    }

}
