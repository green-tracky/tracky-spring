package com.example.tracky.user.runlevel;

import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.user.User;
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
    private final RunLevelRepository runningLevelRepository;

    /**
     * 사용자의 누적 거리를 기반으로 레벨을 업데이트할 필요가 있는지 확인하고 처리합니다.
     *
     * @param user 현재 사용자 엔티티 (영속성 컨텍스트에 의해 관리되는 상태여야 함)
     */
    @Transactional
    public void updateUserLevelIfNeeded(User user) {
        // 1. 사용자의 전체 누적 거리를 DB에서 조회합니다.
        Integer totalDistance = runRecordRepository.findTotalDistanceByUserId(user.getId());

        // 2. 모든 레벨 정보를 DB에서 조회합니다. (가장 높은 레벨부터 정렬)
        List<RunLevel> allLevels = runningLevelRepository.findAllByOrderByMinDistanceDesc();

        // 3. 사용자의 새로운 레벨을 결정합니다.
        RunLevel newLevel = null;
        for (RunLevel level : allLevels) {
            // 사용자의 총 누적 거리가 레벨의 요구 조건(minDistance)보다 크거나 같으면,
            // 해당 레벨이 현재 사용자의 레벨입니다. (가장 높은 레벨부터 검사했으므로 바로 break)
            if (totalDistance >= level.getMinDistance()) {
                newLevel = level;
                break;
            }
        }

        // 4. 레벨 변경이 필요한지 확인하고 업데이트를 수행합니다.
        // - newLevel이 null이 아니어야 하고 (적어도 '옐로우' 레벨은 찾아야 함)
        // - 새로 찾은 레벨이 사용자의 현재 레벨과 달라야 합니다.
        if (newLevel != null && !newLevel.equals(user.getRunLevel())) {
            // 사용자 엔티티의 레벨을 새로운 레벨로 변경합니다.
            user.updateRunLevel(newLevel);
            log.info("레벨업! 사용자 ID: {}, 새로운 레벨: {}", user.getId(), newLevel.getName());

            // 이 메서드는 @Transactional 내에서 호출되므로, 메서드 종료 시 user 객체의 변경사항이
            // DB에 자동으로 반영됩니다(Dirty Checking). userRepository.save(user)를 호출할 필요가 없습니다.
        }
    }

}
