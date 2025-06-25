package com.example.tracky.runrecord;

import com.example.tracky.runrecord.runsegment.RunSegmentRequest;
import com.example.tracky.runrecord.runsegment.runcoordinate.RunCoordinateRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
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
        log.debug("✅ 러닝 기록 아이디: " + runRecord.toString());
    }

    @Test
    void save_test() {
        // given
        RunCoordinateRequest.DTO cDTO = new RunCoordinateRequest.DTO();
        cDTO.setLat(10.0);
        cDTO.setLon(15.0);
        cDTO.setCreatedAt(Timestamp.valueOf("2025-06-22 06:37:10"));

        RunSegmentRequest.DTO sDTO = new RunSegmentRequest.DTO();
        sDTO.setEndDate(Timestamp.valueOf("2025-06-22 06:37:10"));
        sDTO.setCoordinates(List.of(cDTO));
        sDTO.setDistanceMeters(400);
        sDTO.setDurationSeconds(300);

        RunRecordRequest.SaveDTO reqDTO = new RunRecordRequest.SaveDTO();
        reqDTO.setTitle("test 제목");
        reqDTO.setSegments(List.of(sDTO));

        RunRecord runRecord = reqDTO.toEntity(1);

        // when
        RunRecord runRecordPS = runRecordRepository.save(runRecord);

        // eye
        log.debug("✅기록 아이디: " + runRecordPS.getId());
        log.debug("✅구간 아이디: " + runRecordPS.getRunSegments().get(0).getId());
        log.debug("✅좌표 아이디: " + runRecordPS.getRunSegments().get(0).getRunCoordinates().get(0).getId());

    }
}
