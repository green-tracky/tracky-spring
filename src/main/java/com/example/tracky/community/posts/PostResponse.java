package com.example.tracky.community.posts;

import com.example.tracky.community.posts.comments.CommentResponse;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.RunRecordResponse;
import com.example.tracky.runrecord.pictures.PictureResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {

    @Data
    public static class SaveDTO {
        private Integer id;
        private String title;
        private String content;
        private Integer userId;
        private String createdAt;
        private Integer runRecordId;

        public SaveDTO(Post post) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.userId = post.getUser().getId();
            this.createdAt = post.getCreatedAt().toString();
            this.runRecordId = post.getRunRecord().getId();
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

        public ListDTO(Post post, RunRecord runRecord, Integer likeCount, Integer commentCount, Boolean isLiked) {
            this.id = post.getId();
            this.username = post.getUser().getUsername();
            this.content = post.getContent();
            this.createdAt = post.getCreatedAt();
            this.pictures = (runRecord != null && runRecord.getPictures() != null) ?
                    runRecord.getPictures().stream()
                            .map(picture -> new PictureResponse.DTO(picture))
                            .toList()
                    : List.of();
            this.likeCount = likeCount;
            this.commentCount = commentCount;
            this.isLiked = isLiked;
        }
    }

    @Data
    public static class DetailDTO {

        private final Integer id;
        private final String title;
        private final String content;
        private final Integer userId;
        private final RunRecordResponse.DetailDTO runRecord;
        private final List<PictureResponse.DTO> pictures;
        private final List<CommentResponse.DTO> commentDTOs;
        private final Integer likeCount;
        private final Integer commentCount;
        private final Boolean isLiked;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public DetailDTO(Post post, List<PostPicture> postPictures, Integer likeCount, Integer commentCount, Boolean isLiked) {
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
            this.pictures = (postPictures != null) ?
                    postPictures.stream()
                            .map(postPicture -> new PictureResponse.DTO(postPicture.getPicture()))
                            .toList()
                    : List.of();
            this.likeCount = likeCount;
            this.commentCount = commentCount;
            this.isLiked = isLiked;
            this.createdAt = post.getCreatedAt();
            this.updatedAt = post.getUpdatedAt();
        }

        public static List<DetailDTO> toPostResponseDTOs(List<Post> posts) {
            return posts.stream()
                    .map(post -> new DetailDTO(post))
                    .toList();
        }
    }
}
