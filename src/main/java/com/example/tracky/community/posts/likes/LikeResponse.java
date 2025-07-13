package com.example.tracky.community.posts.likes;

import lombok.Data;

public class LikeResponse {

    @Data
    public static class SaveDTO {
        private Integer likeId;
        private Integer likeCount;

        public SaveDTO(Integer likeId, Integer likeCount) {
            this.likeId = likeId;
            this.likeCount = likeCount;
        }
    }

    @Data
    public static class DeleteDTO {
        private Integer likeCount;

        public DeleteDTO(Integer likeCount) {
            this.likeCount = likeCount;
        }
    }
}
