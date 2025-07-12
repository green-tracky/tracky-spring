package com.example.tracky.community.posts.likes;

import com.example.tracky.community.posts.Post;
import com.example.tracky.community.posts.comments.Comment;
import com.example.tracky.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class LikeRequest {

    @Data
    public static class SavePostDTO {
        @NotNull(message = "post의 id가 전달되어야 합니다")
        private Integer postId;

        public Like toEntity(User user, Post post) {
            return Like.builder()
                    .user(user)
                    .post(post)
                    .build();
        }
    }

    @Data
    public static class SaveCommentDTO {
        @NotNull(message = "comment의 id가 전달되어야 합니다")
        private Integer commentId;

        public Like toEntity(User user, Comment comment) {
            return Like.builder()
                    .user(user)
                    .comment(comment)
                    .build();
        }
    }
}
