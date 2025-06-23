package com.example.tracky.runrecord;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tracky._core.error.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.runrecord.RunRecordResponse.DTO;
import com.example.tracky.runrecord.RunRecordResponse.MainPageDTO;
import com.example.tracky.runrecord.utils.RunRecordUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunRecordService {

    private final RunRecordRepository runRecordsRepository;

    public void getRunRecord(Integer id) {
        // 러닝 기록 조회
        RunRecord runRecord = runRecordsRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

    }

    public MainPageDTO getActivitis() {
        List<RunRecord> runRecords = runRecordsRepository.findAllByUserIdJoin(); // 나중에 유저 아이디로 조회 해야함
        // List<RunBadge> runBadges = runRecordsRepository.findAllBadge(); // 나중에 획득한
        // 뱃지만 가져와야함

        Integer totalDistanceMeters = 0;
        Integer totalDurationSeconds = 0; // 총 시간. 초 단위

        // DTO 리스트 수동 생성
        List<DTO> dtoList = new ArrayList<>();
        for (RunRecord record : runRecords) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
            dtoList.add(new DTO(record));
        }

        String avgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

        RunRecord totalRunRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();

        return new RunRecordResponse.MainPageDTO(totalRunRecord, avgPace, dtoList);
    }

    /**
     * 러닝 저장
     * 
     * @param userId
     * @param reqDTO
     */
    @Transactional
    public RunRecordResponse.DTO save(Integer userId, RunRecordRequest.DTO reqDTO) {
        // 엔티티 변환
        RunRecord runRecord = reqDTO.toEntity(userId);

        // 엔티티 저장
        RunRecord runRecordPS = runRecordsRepository.save(runRecord);

        // 응답 DTO 로 변환
        return new RunRecordResponse.DTO(runRecordPS);
    }

}