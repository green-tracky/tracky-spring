package com.example.tracky.runrecord;

import com.example.tracky._core.error.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunRecordService {

    private final RunRecordRepository runRecordsRepository;

    /**
     * 러닝 상세 조회
     *
     * @param id runRecordId
     * @return
     */
    public RunRecordResponse.DetailDTO getRunRecord(User user, Integer id) {
        // 러닝 기록 조회
        RunRecord runRecordPS = runRecordsRepository.findByIdJoin(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

        // 권한 체크
        if (!runRecordPS.getUser().getId().equals(user.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }

        // 러닝 응답 DTO 로 변환
        return new RunRecordResponse.DetailDTO(runRecordPS);
    }

    /**
     * 러닝 저장
     *
     * @param user
     * @param reqDTO
     */
    @Transactional
    public RunRecordResponse.SaveDTO save(User user, RunRecordRequest.SaveDTO reqDTO) {
        // 엔티티 변환
        RunRecord runRecord = reqDTO.toEntity(user);

        // 엔티티 저장
        RunRecord runRecordPS = runRecordsRepository.save(runRecord);

        // 응답 DTO 로 변환
        return new RunRecordResponse.SaveDTO(runRecordPS);
    }

    /**
     * 러닝 삭제
     *
     * @param user
     * @param id   runRecordId
     * @return
     */
    @Transactional
    public void delete(User user, Integer id) {
        // 러닝 기록 조회
        RunRecord runRecordPS = runRecordsRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

        // 권한 체크 -> 나중에 권한체크 로직 빼야함
        if (!runRecordPS.getUser().getId().equals(user.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }

        // 삭제
        runRecordsRepository.delete(runRecordPS);
    }

    @Transactional
    public RunRecordResponse.UpdateDTO update(User user, Integer id, RunRecordRequest.UpdateDTO reqDTO) {
        // 러닝 기록 조회
        RunRecord runRecordPS = runRecordsRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

        // 권한 체크 -> 나중에 권한체크 로직 빼야함
        if (!runRecordPS.getUser().getId().equals(user.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }

        // 러닝 내용 수정
        runRecordPS.update(reqDTO);

        // 응답 DTO 로 반환
        return new RunRecordResponse.UpdateDTO(runRecordPS);
    }
}