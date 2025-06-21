package com.example.tracky.runrecord;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(RunRecordRepository.class)
@DataJpaTest
public class RunRecordRepositoryTest {

    @Autowired
    private RunRecordRepository runRecordRepository;

    @Test
    void findById_test() {
        // given
        Integer id = 1;

        // when
        RunRecord runRecord = runRecordRepository.findById(id)
                .orElseThrow();

        // eye
        System.out.println(runRecord.getId());
    }
}
