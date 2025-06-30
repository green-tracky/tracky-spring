package com.example.tracky.community.post.comment;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    @Data
    @Getter
    public static class CommentResponseDTO {

        private final Integer id;
        private final Integer postId;
        private final Integer userId;
        private final String username;
        private final String content;
        private final Integer parentId;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;
        private final List<CommentResponseDTO> children;

        // ✅ 생성자 추가
        public CommentResponseDTO(Integer id, Integer postId, Integer userId, String username,
                                  String content, Integer parentId,
                                  LocalDateTime createdAt, LocalDateTime updatedAt,
                                  List<CommentResponseDTO> children) {
            this.id = id;
            this.postId = postId;
            this.userId = userId;
            this.username = username;
            this.content = content;
            this.parentId = parentId;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.children = children;
        }

        // ✅ 정적 팩토리 메서드 그대로 사용
        public static CommentResponseDTO fromEntity(Comment comment) {
            return new CommentResponseDTO(
                    comment.getId(),
                    comment.getPost().getId(),
                    comment.getUser().getId(),
                    comment.getUser().getUsername(),
                    comment.getContent(),
                    comment.getParent() != null ? comment.getParent().getId() : null,
                    comment.getCreatedAt(),
                    comment.getUpdatedAt(),
                    comment.getChildren().stream()
                            .map(CommentResponseDTO::fromEntity)
                            .toList()
            );
        }
    }
}
