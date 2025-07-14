package com.example.tracky.community.posts.comments;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    @Data
    public static class CommentsList {
        private Integer next;
        private Integer current;
        private Integer totalCount;
        private Integer totalPage;
        private Boolean isLast;
        List<ParentDTO> comments;

        public CommentsList(Integer current, Integer totalCount, Integer parentCount, List<CommentResponse.ParentDTO> parentDTOS) {
            this.next = current + 1;
            this.current = current;
            this.totalCount = totalCount; // given
            this.totalPage = makeTotalPage(parentCount); // 2
            this.isLast = this.totalPage.equals(current);
            this.comments = parentDTOS;
        }

        private Integer makeTotalPage(int parentCount) {
            int rest = parentCount % 5 > 0 ? 1 : 0; // 6 -> 0, 7 -> 1, 8 -> 2
            return parentCount / 5 + rest;
        }
    }

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


        public ParentDTO(Comment comment) {
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

    @Data
    public static class UpdateDTO {

        private final Integer id;
        private final Integer postId;
        private final Integer userId;
        private final String username;
        private final String content;
        private final Integer parentId;
        private final LocalDateTime updatedAt;

        public UpdateDTO(Comment comment) {
            this.id = comment.getId();
            this.postId = comment.getPost().getId();
            this.userId = comment.getUser().getId();
            this.username = comment.getUser().getUsername();
            this.content = comment.getContent();
            this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
            this.updatedAt = comment.getUpdatedAt();
        }
    }
}
