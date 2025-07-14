package com.example.tracky.user.runlevel;

import com.example.tracky.user.runlevel.utils.RunLevelUtil;
import lombok.Data;

import java.util.List;

public class RunLevelResponse {

    @Data
    public static class DTO {
        private Integer id;
        private String name; // 레벨 이름
        private Integer minDistance; // 해당 레벨의 조건 범위 시작 (m)
        private Integer maxDistance; // 해당 레벨의 조건 범위 끝 (m)
        private String description; // 레벨 설명 (예: "0~49.99킬로미터" 등)
        private Integer sortOrder; // 레벨 정렬용 값

        public DTO(RunLevel runLevel) {
            this.id = runLevel.getId();
            this.name = runLevel.getName();
            this.minDistance = runLevel.getMinDistance();
            this.maxDistance = runLevel.getMaxDistance();
            this.description = runLevel.getDescription();
            this.sortOrder = runLevel.getSortOrder();
        }

    }

    @Data
    public static class ListDTO {
        private Integer totalDistance; // 사용자 누적 거리
        private Integer distanceToNextLevel; // 다음 레벨까지 필요한 거리
        private List<RunLevelDTO> runLevels;

        /**
         * <pre>
         * 서비스 레이어에서 준비한 재료들(누적 거리, 현재 레벨, 모든 레벨 리스트)을 받아
         * 최종 응답 DTO를 생성하는 생성자입니다. 모든 계산 로직이 여기에 집중됩니다.
         * </pre>
         *
         * @param currentRunLevel 사용자의 현재 레벨 정보 (DB에 저장된 상태 기준)
         * @param runLevels       시스템의 모든 레벨 정보 리스트 (정렬된 상태)
         * @param totalDistance   사용자의 총 누적 거리
         */
        public ListDTO(RunLevel currentRunLevel, List<RunLevel> runLevels, Integer totalDistance) {
            // 1. 누적거리 저장
            this.totalDistance = totalDistance;

            // 2. '다음 레벨까지 남은 거리'를 계산합니다.
            this.distanceToNextLevel = RunLevelUtil.getDistanceToNextLevel(currentRunLevel, runLevels, totalDistance);

            // 3. '레벨 목록 (runLevels)'을 생성합니다.
            // 모든 레벨 정보를 순회하며, 각 레벨이 현재 사용자의 레벨인지 판별하여 DTO 리스트를 만듭니다.
            this.runLevels = runLevels.stream()
                    .map(level -> {
                        // 현재 순회중인 레벨의 ID가 사용자의 현재 레벨 ID와 같은지 확인합니다.
                        boolean isCurrent = level.getId().equals(currentRunLevel.getId());
                        return new RunLevelDTO(level, isCurrent);
                    })
                    .toList();
        }

        @Data
        class RunLevelDTO {
            private Integer id;
            private String name; // 레벨 이름
            private Integer minDistance; // 해당 레벨의 조건 범위 시작 (m)
            private Integer maxDistance; // 해당 레벨의 조건 범위 끝 (m)
            private String description; // 레벨 설명 (예: "0~49.99킬로미터" 등)
            private Integer sortOrder; // 레벨 정렬용 값
            private Boolean isCurrent; // 현재 레벨 표시

            /**
             * runLevel과, 이 레벨이 현재 사용자의 레벨인지 여부를 받아 DTO를 생성합니다.
             *
             * @param runLevel
             * @param isCurrent 이 레벨이 현재 사용자의 레벨이면 true
             */
            public RunLevelDTO(RunLevel runLevel, boolean isCurrent) {
                this.id = runLevel.getId();
                this.name = runLevel.getName();
                this.minDistance = runLevel.getMinDistance();
                this.maxDistance = runLevel.getMaxDistance();
                this.description = runLevel.getDescription();
                this.sortOrder = runLevel.getSortOrder();
                this.isCurrent = isCurrent;
            }
        }

    }

}
