package com.example.tracky.community.posts.comments;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    @Data
    public static class ParentDTO {

        private final Integer id;
        private final Integer postId;
        private final Integer userId;
        private final String username;
        private final String content;
        private final Integer parentId;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;
        private final List<ChildDTO> children;
        private Integer prev;
        private Integer next;
        private Integer current;
        private Integer totalCount;
        private Integer totalPage;
        private Boolean isFirst; // current == 0
        private Boolean isLast; // totalCount, size=3, totalPage == current

        public ParentDTO(Comment comment, List<Comment> reply, Integer current, Integer totalCount, Integer parentCount) {
            this.id = comment.getId();
            this.postId = comment.getPost().getId();
            this.userId = comment.getUser().getId();
            this.username = comment.getUser().getUsername();
            this.content = comment.getContent();
            this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
            this.createdAt = comment.getCreatedAt();
            this.updatedAt = comment.getUpdatedAt();
            this.children = comment.getChildren().stream()
                    .map(child -> new ChildDTO(child))
                    .toList();
            this.prev = current - 1;
            this.next = current + 1;
            this.current = current;
            this.totalCount = totalCount; // given
            this.totalPage = makeTotalPage(parentCount); // 2
            this.isFirst = current == 1;
            this.isLast = totalPage == current;
        }

        private Integer makeTotalPage(int parentCount) {
            int rest = parentCount % 5 > 0 ? 1 : 0; // 6 -> 0, 7 -> 1, 8 -> 2
            return parentCount / 5 + rest;
        }

        @Data
        class ChildDTO {
            private final Integer id;
            private final Integer postId;
            private final Integer userId;
            private final String username;
            private final String content;
            private final Integer parentId;
            private final LocalDateTime createdAt;
            private final LocalDateTime updatedAt;

            public ChildDTO(Comment comment) {
                this.id = comment.getId();
                this.postId = comment.getPost().getId();
                this.userId = comment.getUser().getId();
                this.username = comment.getUser().getUsername();
                this.content = comment.getContent();
                this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
                this.createdAt = comment.getCreatedAt();
                this.updatedAt = comment.getUpdatedAt();
            }
        }
    }

    @Data
    public static class SaveDTO {

        private final Integer id;
        private final Integer postId;
        private final Integer userId;
        private final String username;
        private final String content;
        private final Integer parentId;
        private final LocalDateTime createdAt;

        public SaveDTO(Comment comment) {
            this.id = comment.getId();
            this.postId = comment.getPost().getId();
            this.userId = comment.getUser().getId();
            this.username = comment.getUser().getUsername();
            this.content = comment.getContent();
            this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
            this.createdAt = comment.getCreatedAt();
        }
    }
}
