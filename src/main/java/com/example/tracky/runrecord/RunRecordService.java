package com.example.tracky.runrecord;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tracky._core.error.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;

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

    /**
     * 러닝 저장
     * 
     * @param userId
     * @param reqDTO
     */
    @Transactional
    public void save(Integer userId, RunRecordRequest.DTO reqDTO) {

    }

}