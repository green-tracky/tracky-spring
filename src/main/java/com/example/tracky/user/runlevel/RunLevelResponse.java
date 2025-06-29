package com.example.tracky.user.runlevel;

import lombok.Data;

public class RunLevelResponse {

    @Data
    public static class DTO {
        private Integer id;
        private String name; // 레벨 이름
        private Integer minDistance; // 해당 레벨의 조건 범위 시작 (m)
        private Integer maxDistance; // 해당 레벨의 조건 범위 끝 (m)
        private String description; // 레벨 설명 (예: "초보 러너" 등)
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


}
