package com.example.tracky.runrecord;

import com.example.tracky._core.error.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RunRecordService {

    private final RunRecordRepository runRecordsRepository;

    /**
     * 러닝 상세 조회
     *
     * @param runRecordId
     * @return
     */
    public RunRecordResponse.DetailDTO getRunRecord(Integer runRecordId) {
        // 러닝 기록 조회
        RunRecord runRecord = runRecordsRepository.findByIdJoin(runRecordId)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

        // 러닝 응답 DTO 로 변환
        return new RunRecordResponse.DetailDTO(runRecord);
    }

    /**
     * 러닝 저장
     *
     * @param userId
     * @param reqDTO
     */
    @Transactional
    public RunRecordResponse.SaveDTO save(Integer userId, RunRecordRequest.SaveDTO reqDTO) {
        // 엔티티 변환
        RunRecord runRecord = reqDTO.toEntity(userId);

        // 엔티티 저장
        RunRecord runRecordPS = runRecordsRepository.save(runRecord);

        // 응답 DTO 로 변환
        return new RunRecordResponse.SaveDTO(runRecordPS);
    }

}