package com.example.tracky.community.posts;


import com.example.tracky.community.posts.comments.CommentResponse;
import com.example.tracky.runrecord.RunRecordResponse;
import com.example.tracky.runrecord.pictures.PictureResponse;
import com.example.tracky.user.User;
import com.example.tracky.user.UserResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostResponse {

    @Data
    public static class SaveDTO {
        private Integer id;
        private String content;
        private Integer userId;
        private LocalDateTime createdAt;
        private Integer runRecordId;

        public SaveDTO(Post post) {
            this.id = post.getId();
            this.content = post.getContent();
            this.userId = post.getUser().getId();
            this.createdAt = post.getCreatedAt();
            this.runRecordId = post.getRunRecord() != null ? post.getRunRecord().getId() : null;
        }
    }


    @Data
    public static class ListDTO {
        private Integer id;
        private String content;
        private LocalDateTime createdAt;
        private Integer commentCount;
        private Integer likeCount;
        private Boolean isLiked;
        private List<PictureResponse.DTO> pictures;
        private UserResponse.PostUserDTO user;

        public ListDTO(Post post, Integer likeCount, Integer commentCount, Boolean isLiked) {
            this.id = post.getId();
            this.content = post.getContent();
            this.createdAt = post.getCreatedAt();
            this.likeCount = likeCount;
            this.commentCount = commentCount;
            this.isLiked = isLiked;
            this.pictures = post.getRunRecord() != null ? post.getRunRecord().getPictures().stream().map(p -> new PictureResponse.DTO(p)).toList() : new ArrayList<>();
            this.user = new UserResponse.PostUserDTO(post.getUser());
        }
    }

    @Data
    public static class DetailDTO {

        private Integer id;
        private String content;
        private UserResponse.PostUserDTO user;
        private RunRecordResponse.DetailDTO runRecord;
        private CommentResponse.CommentsList commentsInfo;
        private Integer likeCount;
        private Integer commentCount;
        private Boolean isLiked;
        private Boolean isOwner;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public DetailDTO(Post post, CommentResponse.CommentsList commentsInfo, Integer likeCount, Integer commentCount, Boolean isLiked, User user) {
            this.id = post.getId();
            this.content = post.getContent();
            this.user = new UserResponse.PostUserDTO(post.getUser());
            this.runRecord = post.getRunRecord() != null
                    ? new RunRecordResponse.DetailDTO(post.getRunRecord())
                    : null;
            this.commentsInfo = commentsInfo;
            this.likeCount = likeCount;
            this.commentCount = commentCount;
            this.isLiked = isLiked;
            this.isOwner = post.getUser().getId().equals(user.getId());
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

        public UpdateDTO(Post post) {
            this.id = post.getId();
            this.content = post.getContent();
            this.createdAt = post.getCreatedAt();
            this.updatedAt = post.getUpdatedAt();
        }
    }
}
