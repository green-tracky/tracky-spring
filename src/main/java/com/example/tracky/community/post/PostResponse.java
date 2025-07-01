package com.example.tracky.community.post;

import com.example.tracky.community.post.comment.CommentResponse;
import com.example.tracky.runrecord.RunRecordResponse;
import com.example.tracky.runrecord.picture.Picture;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {

    @Data
    public static class listDTO {
        private final Integer id;
        private final String username;
        private final String content;
        private final LocalDateTime createdAt;
        private final List<Picture> pictures;
        private final Integer loveCount;
        private final Integer commentCount;
        private final Boolean isLiked;

        public listDTO(Post post, Integer loveCount, Integer commentCount, Boolean isLiked) {
            this.id = post.getId();
            this.username = post.getUser().getUsername();
            this.content = post.getContent();
            this.createdAt = post.getCreatedAt();
            this.pictures = post.getRunRecord().getPictures();
            this.loveCount = loveCount;
            this.commentCount = commentCount;
            this.isLiked = isLiked;
        }
    }

    @Data
    public static class DTO {

        private final Integer id;
        private final String title;
        private final String content;
        private final Integer userId;
        private final RunRecordResponse.DetailDTO runRecord;
        private final List<CommentResponse.DTO> commentDTOs;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public DTO(Post post) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.userId = post.getUser().getId();
            this.runRecord = post.getRunRecord() != null
                    ? new RunRecordResponse.DetailDTO(post.getRunRecord())
                    : null;
            this.commentDTOs = post.getComments().stream()
                    .map(comment -> new CommentResponse.DTO(comment)) // 생성자 직접 호출
                    .toList();
            this.createdAt = post.getCreatedAt();
            this.updatedAt = post.getUpdatedAt();
        }

        public static List<DTO> toPostResponseDTOs(List<Post> posts) {
            return posts.stream()
                    .map(post -> new DTO(post))
                    .toList();
        }
    }
}
