package com.example.tracky.community.post;

import com.example.tracky.community.post.comment.CommentResponse;
import com.example.tracky.runrecord.RunRecordResponse;
import com.example.tracky.runrecord.picture.PictureResponse;
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
            this.pictures = (postPictures != null) ?
                    postPictures.stream()
                            .map(postPicture -> new PictureResponse.DTO(postPicture.getPicture()))
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
        private final String content;
        private final Integer userId;
        private final RunRecordResponse.DetailDTO runRecord;
        private final List<CommentResponse.DTO> commentDTOs;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public DetailDTO(Post post) {
            this.id = post.getId();
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

        public static List<DetailDTO> toPostResponseDTOs(List<Post> posts) {
            return posts.stream()
                    .map(post -> new DetailDTO(post))
                    .toList();
        }
    }

    @Data
    public static class UpdateDTO {
        private String content;
        private LocalDateTime updatedAt;
        private Integer runRecordId;
        private List<Integer> pictureIds;

        public UpdateDTO(Post post) {
            this.content = post.getContent();
            this.updatedAt = post.getUpdatedAt();
            this.runRecordId = post.getRunRecord().getId();
            this.pictureIds = post.getPostPictures().stream()
                    .map(pp -> pp.getPicture().getId())
                    .toList();
        }

    }
}
