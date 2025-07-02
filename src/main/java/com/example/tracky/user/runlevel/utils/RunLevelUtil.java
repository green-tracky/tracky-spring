package com.example.tracky.user.runlevel.utils;

import com.example.tracky.user.runlevel.RunLevel;

import java.util.List;
import java.util.stream.IntStream;

public class RunLevelUtil {

    /**
     * <pre>
     * 다음 러닝레벨까지 필요한 거리
     * 마지막 레벨이면 null 을 반환
     * </pre>
     *
     * @param currentRunLevel
     * @param runLevels       오름차순으로 가져와야 함
     * @param totalDistance   - 누적거리
     * @return
     */
    public static Integer getDistanceToNextLevel(RunLevel currentRunLevel, List<RunLevel> runLevels, Integer totalDistance) {
        // 마지막 레벨이거나, 알 수 없는 이유로 현재 레벨을 못 찾은 경우 null로 설정합니다.
        Integer distanceToNextLevel = null;

        // 먼저 사용자의 현재 레벨이 전체 리스트에서 몇 번째인지 찾습니다.
        // (레벨의 개수가 100개가 넘어가면 for 문이 더 좋다)
        int currentIndex = IntStream.range(0, runLevels.size())
                .filter(i -> runLevels.get(i).getId().equals(currentRunLevel.getId()))
                .findFirst()
                .orElse(-1);

        // 현재 레벨이 마지막 레벨이 아닌 경우에만 남은 거리를 계산합니다.
        if (currentIndex != -1 && currentIndex < runLevels.size() - 1) {
            RunLevel nextLevel = runLevels.get(currentIndex + 1); // 러닝레벨 목록을 오름차순으로 가져와야 한다
            int remaining = nextLevel.getMinDistance() - totalDistance;
            // ⬇ 관리자가 임의로 사용자의 누적거리를 수정했을 때를 대비해서 방어적 코드를 추가
            distanceToNextLevel = Math.max(0, remaining);
        }

        return distanceToNextLevel;
    }
}
