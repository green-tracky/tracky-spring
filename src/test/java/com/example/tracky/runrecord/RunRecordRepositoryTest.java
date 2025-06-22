package com.example.tracky.runrecord;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.example.tracky.runrecord.runsegment.RunSegmentRequest;
import com.example.tracky.runrecord.runsegment.runcoordinate.RunCoordinateRequest;

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

        RunRecordRequest.DTO reqDTO = new RunRecordRequest.DTO();
        reqDTO.setTitle("test 제목");
        reqDTO.setSegments(List.of(sDTO));

        RunRecord runRecord = reqDTO.toEntity(1);

        // when
        RunRecord runRecordPS = runRecordRepository.save(runRecord);

        // eye
        System.out.println("기록 아이디" + runRecordPS.getId());
        System.out.println("구간 아이디" + runRecordPS.getRunSegments().get(0).getId());
        System.out.println("좌표 아이디" + runRecordPS.getRunSegments().get(0).getRunCoordinates().get(0).getId());
    }
}
