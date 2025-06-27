package com.example.tracky.runrecord;

import com.example.tracky._core.error.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchv;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvService;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunRecordService {

    private final RunRecordRepository runRecordRepository;
    private final RunBadgeAchvService runBadgeAchvService;

    /**
     * 러닝 상세 조회
     *
     * @param id runRecordId
     * @return
     */
    public RunRecordResponse.DetailDTO getRunRecord(User user, Integer id) {
        // 러닝 기록 조회
        RunRecord runRecordPS = runRecordRepository.findByIdJoin(id)
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
        // 1. DTO를 엔티티로 변환합니다.
        RunRecord runRecord = reqDTO.toEntity(user);

        // 2. 달리기 기록 엔티티를 데이터베이스에 저장합니다.
        RunRecord runRecordPS = runRecordRepository.save(runRecord);

        // 3. 뱃지 서비스를 호출하여, 저장된 기록에 대해 획득 가능한 모든 뱃지를 확인하고 부여합니다.
        // 이 과정에서 새로 획득한 뱃지 목록을 반환받습니다.
        List<RunBadgeAchv> awardedBadges = runBadgeAchvService.checkAndAwardRunBadges(runRecordPS);

        // 4. 최종적으로, 저장된 기록과 새로 획득한 뱃지 목록을 DTO로 감싸 컨트롤러에 반환합니다.
        return new RunRecordResponse.SaveDTO(runRecordPS, awardedBadges);

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
        RunRecord runRecordPS = runRecordRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

        // 권한 체크 -> 나중에 권한체크 로직 빼야함
        if (!runRecordPS.getUser().getId().equals(user.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }

        // 삭제
        runRecordRepository.delete(runRecordPS);
    }

    @Transactional
    public RunRecordResponse.UpdateDTO update(User user, Integer id, RunRecordRequest.UpdateDTO reqDTO) {
        // 러닝 기록 조회
        RunRecord runRecordPS = runRecordRepository.findById(id)
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