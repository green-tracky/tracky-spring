package com.example.tracky.community.posts.likes;

import com.example.tracky.community.posts.Post;
import com.example.tracky.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class LikeRequest {

    @Data
    public static class SaveDTO {
        @NotNull(message = "post의 id가 전달되어야 합니다")
        private Integer postId;

        public Like toEntity(Integer userId) {
            return Like.builder()
                    .post(Post.builder().id(postId).build())
                    .user(User.builder().id(userId).build())
                    .build();
        }
    }
}
