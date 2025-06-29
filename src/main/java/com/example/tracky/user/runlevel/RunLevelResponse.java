package com.example.tracky.user.runlevel;

import lombok.Data;

import java.util.List;
import java.util.stream.IntStream;

public class RunLevelResponse {

    @Data
    public static class DTO {
        private Integer id;
        private String name; // 레벨 이름
        private Integer minDistance; // 해당 레벨의 조건 범위 시작 (m)
        private Integer maxDistance; // 해당 레벨의 조건 범위 끝 (m)
        private String description; // 레벨 설명 (예: "0~49.99킬로미터" 등)
        private String imageUrl; // 레벨에 대응하는 이미지 URL
        private Integer sortOrder; // 레벨 정렬용 값

        public DTO(RunLevel runLevel) {
            this.id = runLevel.getId();
            this.name = runLevel.getName();
            this.minDistance = runLevel.getMinDistance();
            this.maxDistance = runLevel.getMaxDistance();
            this.description = runLevel.getDescription();
            this.imageUrl = runLevel.getImageUrl();
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
         * @param currentLevel  사용자의 현재 레벨 정보 (DB에 저장된 상태 기준)
         * @param runLevels     시스템의 모든 레벨 정보 리스트 (정렬된 상태)
         * @param totalDistance 사용자의 총 누적 거리
         */
        public ListDTO(RunLevel currentLevel, List<RunLevel> runLevels, Integer totalDistance) {
            // 1. 누적거리 저장
            this.totalDistance = totalDistance;

            // 2. '다음 레벨까지 남은 거리'를 계산합니다.
            // 먼저 사용자의 현재 레벨이 전체 리스트에서 몇 번째인지 찾습니다.
            // (레벨의 개수가 100개가 넘어가면 for 문이 더 좋다)
            int currentIndex = IntStream.range(0, runLevels.size())
                    .filter(i -> runLevels.get(i).getId().equals(currentLevel.getId()))
                    .findFirst()
                    .orElse(-1);

            // 현재 레벨이 마지막 레벨이 아닌 경우에만 남은 거리를 계산합니다.
            if (currentIndex != -1 && currentIndex < runLevels.size() - 1) {
                RunLevel nextLevel = runLevels.get(currentIndex + 1);
                int remaining = nextLevel.getMinDistance() - totalDistance;
                // ⬇ 관리자가 임의로 사용자의 누적거리를 수정했을 때를 대비해서 방어적 코드를 추가
                this.distanceToNextLevel = Math.max(0, remaining);
            } else {
                // 마지막 레벨이거나, 알 수 없는 이유로 현재 레벨을 못 찾은 경우 null로 설정합니다.
                this.distanceToNextLevel = null;
            }

            // 3. '레벨 목록 (runLevels)'을 생성합니다.
            // 모든 레벨 정보를 순회하며, 각 레벨이 현재 사용자의 레벨인지 판별하여 DTO 리스트를 만듭니다.
            this.runLevels = runLevels.stream()
                    .map(level -> {
                        // 현재 순회중인 레벨의 ID가 사용자의 현재 레벨 ID와 같은지 확인합니다.
                        boolean isCurrent = level.getId().equals(currentLevel.getId());
                        // RunLevel 엔티티와 isCurrent 플래그를 사용하여 DTO를 생성합니다.
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
            private String imageUrl; // 레벨에 대응하는 이미지 URL
            private Integer sortOrder; // 레벨 정렬용 값
            private Boolean isCurrent;

            /**
             * RunLevel 엔티티와, 이 레벨이 현재 사용자의 레벨인지 여부를 받아 DTO를 생성합니다.
             *
             * @param runLevel  원본 RunLevel 엔티티
             * @param isCurrent 이 레벨이 현재 사용자의 레벨이면 true
             */
            public RunLevelDTO(RunLevel runLevel, boolean isCurrent) {
                this.id = runLevel.getId();
                this.name = runLevel.getName();
                this.minDistance = runLevel.getMinDistance();
                this.maxDistance = runLevel.getMaxDistance();
                this.description = runLevel.getDescription();
                this.imageUrl = runLevel.getImageUrl();
                this.sortOrder = runLevel.getSortOrder();
                this.isCurrent = isCurrent;
            }
        }

    }

}
