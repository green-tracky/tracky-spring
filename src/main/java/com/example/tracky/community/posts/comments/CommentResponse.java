package com.example.tracky.community.posts.comments;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    @Data
    public static class DTO {

        private final Integer id;
        private final Integer postId;
        private final Integer userId;
        private final String username;
        private final String content;
        private final Integer parentId;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;
        private final List<DTO> children;

        public DTO(Comment comment) {
            this.id = comment.getId();
            this.postId = comment.getPost().getId();
            this.userId = comment.getUser().getId();
            this.username = comment.getUser().getUsername();
            this.content = comment.getContent();
            this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
            this.createdAt = comment.getCreatedAt();
            this.updatedAt = comment.getUpdatedAt();
            this.children = comment.getChildren().stream()
                    .map(child -> new DTO(child))
                    .toList();
        }
    }
}
