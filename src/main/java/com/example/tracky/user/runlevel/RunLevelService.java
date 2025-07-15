package com.example.tracky.user.runlevel;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RunLevelService {

    private final RunRecordRepository runRecordRepository;
    private final RunLevelRepository runLevelRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 누적 거리를 기반으로 레벨을 업데이트할 필요가 있는지 확인하고 처리합니다.
     *
     * @param user 현재 사용자
     */
    @Transactional
    public void updateUserLevelIfNeeded(User user) {
        // 1. 사용자의 전체 누적 거리를 DB에서 조회합니다.
        Integer totalDistance = runRecordRepository.findTotalDistanceByUserId(user.getId());

        // 2. 모든 레벨 정보를 DB에서 조회합니다. (가장 높은 레벨부터 정렬)
        List<RunLevel> runLevelsPS = runLevelRepository.findAllByOrderBySortOrderDesc();

        // 3. 사용자의 새로운 레벨을 결정합니다.
        // (레벨의 개수가 100개가 넘어가면 for 문이 더 좋다)
        RunLevel newRunLevelPS = runLevelsPS.stream()
                .filter(level -> totalDistance >= level.getMinDistance())
                .findFirst()
                .orElse(null);

        // 4. 레벨 변경이 필요한지 확인하고 업데이트를 수행합니다.
        // - newLevel이 null이 아니어야 하고 (적어도 '옐로우' 레벨은 찾아야 함)
        // - 새로 찾은 레벨이 사용자의 현재 레벨과 달라야 합니다.
        if (newRunLevelPS != null && !newRunLevelPS.equals(user.getRunLevel())) {
            // 사용자 레벨을 새로운 레벨로 변경합니다.
            user.updateRunLevel(newRunLevelPS);
            log.info("레벨업! 사용자 ID: {}, 새로운 레벨: {}", user.getId(), newRunLevelPS.getName());
        }
    }

    public RunLevelResponse.ListDTO getRunLevels(OAuthProfile sessionProfile) {
        // 1. 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 2. 러닝레벨들 조회
        List<RunLevel> runLevelsPS = runLevelRepository.findAllByOrderBySortOrderAsc();

        // 3. 유저의 누적 거리 조회
        Integer totalDistance = runRecordRepository.findTotalDistanceByUserId(userPS.getId());

        log.info("{}({})이 레벨 목록을 조회합니다.", userPS.getUsername(), userPS.getId());

        return new RunLevelResponse.ListDTO(userPS.getRunLevel(), runLevelsPS, totalDistance);
    }
}
