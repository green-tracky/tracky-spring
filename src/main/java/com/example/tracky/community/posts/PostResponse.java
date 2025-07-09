package com.example.tracky.community.posts;


import com.example.tracky.community.posts.comments.CommentResponse;
import com.example.tracky.runrecord.RunRecordResponse;
import com.example.tracky.runrecord.pictures.PictureResponse;
import com.example.tracky.user.UserResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {

    @Data
    public static class SaveDTO {
        private Integer id;
        private String content;
        private Integer userId;
        private LocalDateTime createdAt;
        private Integer runRecordId;
        private List<Integer> pictureIds;

        public SaveDTO(Post post) {
            this.id = post.getId();
            this.content = post.getContent();
            this.userId = post.getUser().getId();
            this.createdAt = post.getCreatedAt();
            this.runRecordId = post.getRunRecord().getId();
            this.pictureIds = post.getPostPictures().stream()
                    .map(pp -> pp.getPicture().getId())
                    .toList();
        }
    }


    @Data
    public static class ListDTO {
        private final Integer id;
        private final String username;
        private final String content;
        private final LocalDateTime createdAt;
        private final List<PictureResponse.DTO> pictures;
        private final Integer likeCount;
        private final Integer commentCount;
        private final Boolean isLiked;

        public ListDTO(Post post, List<PostPicture> postPictures, Integer likeCount, Integer commentCount, Boolean isLiked) {
            this.id = post.getId();
            this.username = post.getUser().getUsername();
            this.content = post.getContent();
            this.createdAt = post.getCreatedAt();
            this.pictures = postPictures.stream()
                    .map(postPicture -> new PictureResponse.DTO(postPicture.getPicture()))
                    .toList();
            this.likeCount = likeCount;
            this.commentCount = commentCount;
            this.isLiked = isLiked;
        }
    }

    @Data
    public static class DetailDTO {

        private final Integer id;
        private final String content;
        private final UserResponse.PostUserDTO user; // User 엔티티를 -> DTO 로
        private final RunRecordResponse.PostRunRecordDTO runRecord;
        private final List<PictureResponse.DTO> pictures;
        private final List<CommentResponse.DTO> comments;
        private final Integer likeCount;
        private final Integer commentCount;
        private final Boolean isLiked;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public DetailDTO(Post post, List<CommentResponse.DTO> comments, List<PostPicture> postPictures, Integer likeCount, Integer commentCount, Boolean isLiked) {
            this.id = post.getId();
            this.content = post.getContent();
            this.user = new UserResponse.PostUserDTO(post.getUser());
            this.runRecord = post.getRunRecord() != null
                    ? new RunRecordResponse.PostRunRecordDTO(post.getRunRecord())
                    : null;
            this.comments = comments;
            this.pictures = postPictures.stream()
                    .map(postPicture -> new PictureResponse.DTO(postPicture.getPicture()))
                    .toList();
            this.likeCount = likeCount;
            this.commentCount = commentCount;
            this.isLiked = isLiked;
            this.createdAt = post.getCreatedAt();
            this.updatedAt = post.getUpdatedAt();
        }

    }

    @Data
    public static class UpdateDTO {
        private Integer id;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Integer runRecordId;
        private List<Integer> pictureIds;

        public UpdateDTO(Post post) {
            this.id = post.getId();
            this.content = post.getContent();
            this.createdAt = post.getCreatedAt();
            this.updatedAt = post.getUpdatedAt();
            this.runRecordId = post.getRunRecord().getId();
            this.pictureIds = post.getPostPictures().stream()
                    .map(pp -> pp.getPicture().getId())
                    .toList();
        }
    }
}
