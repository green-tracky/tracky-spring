package com.example.tracky.runrecord;

import com.example.tracky.runrecord.runsegments.RunSegmentRequest;
import com.example.tracky.runrecord.runsegments.runcoordinates.RunCoordinate;
import com.example.tracky.runrecord.runsegments.runcoordinates.RunCoordinateRepository;
import com.example.tracky.runrecord.runsegments.runcoordinates.RunCoordinateRequest;
import com.example.tracky.user.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Import({RunRecordRepository.class, RunCoordinateRepository.class})
@DataJpaTest
public class RunRecordRepositoryTest {

    @Autowired
    private RunRecordRepository runRecordRepository;

    @Autowired
    private RunCoordinateRepository runCoordinateRepository;

    // ⭐️ [수정 1] 날짜/시간 포맷을 미리 정의해두면 재사용하기 편리합니다.
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void findByIdJoin_test() {
        // given
        Integer runRecordId = 1;

        // when
        RunRecord runRecord = runRecordRepository.findByIdJoin(runRecordId)
                .orElseThrow();

        // eye
        log.debug("✅ 러닝 기록 아이디: " + runRecord.getId());
    }

    @Test
    void save_test() {
        // given
        RunCoordinateRequest.DTO cDTO = new RunCoordinateRequest.DTO();
        cDTO.setLat(10.0);
        cDTO.setLon(15.0);
        cDTO.setRecordedAt(LocalDateTime.parse("2025-06-22 06:37:10", formatter));

        RunSegmentRequest.DTO sDTO = new RunSegmentRequest.DTO();
        sDTO.setEndDate(LocalDateTime.parse("2025-06-22 06:37:10", formatter));
        sDTO.setCoordinates(List.of(cDTO));
        sDTO.setDistanceMeters(400);
        sDTO.setDurationSeconds(300);

        RunRecordRequest.SaveDTO reqDTO = new RunRecordRequest.SaveDTO();
        reqDTO.setTitle("test 제목");
        reqDTO.setSegments(List.of(sDTO));

        User user = User.builder().id(1).build();

        RunRecord runRecord = reqDTO.toEntity(user);

        // when
        RunRecord runRecordPS = runRecordRepository.save(runRecord);

        // eye
        log.debug("✅기록 아이디: " + runRecordPS.getId());
        log.debug("✅구간 아이디: " + runRecordPS.getRunSegments().get(0).getId());
        log.debug("✅좌표 아이디: " + runRecordPS.getRunSegments().get(0).getRunCoordinate().getId());

    }

    @Test
    void delete_test() {
        // given
        Integer runRecordId = 1;
        Integer runCoordinateId = 1;

        RunRecord runRecordPS = runRecordRepository.findByIdJoin(runRecordId).orElseThrow();
        // 배치 로딩 확인
        runRecordPS.getRunSegments().get(0).getRunCoordinate().getCreatedAt();

        // when
        runRecordRepository.delete(runRecordPS);

        // eye
        Optional<RunRecord> runRecordPS2 = runRecordRepository.findById(runRecordId);
        Optional<RunCoordinate> runCoordinate = runCoordinateRepository.findById(runCoordinateId);

        if (runRecordPS2.isPresent()) {
            log.debug("✅러닝 확인:" + runRecordPS2.get().getId());
        } else {
            log.debug("✅러닝 잘 삭제됨");
        }
        if (runCoordinate.isPresent()) {
            log.debug("✅좌표 확인:" + runCoordinate.get().getId());
        } else {
            log.debug("✅좌표 잘 삭제됨");
        }

    }
}
