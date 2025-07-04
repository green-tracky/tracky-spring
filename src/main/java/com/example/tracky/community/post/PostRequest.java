package com.example.tracky.community.post;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.picture.Picture;
import com.example.tracky.user.User;
import lombok.Data;

import java.util.List;

public class PostRequest {

    @Data
    public static class UpdateDTO {

        private String content;

        private Integer runRecordId;

        private List<Integer> picturesId;

    }

    @Data
    public static class SaveDTO {

        private String content;

        private Integer runRecordId;

        private List<Integer> picturesId;


        public Post toEntity(User user, RunRecord runRecord, List<Picture> pictures) {
            Post post = Post.builder()
                    .content(content)
                    .user(user) // user객체 필요
                    .runRecord(runRecord)
                    .build();

            List<PostPicture> postpictures = pictures.stream()
                    .map(picture -> PostPicture.builder().post(post).picture(picture).build())
                    .toList();

            post.postpictures.addAll(postpictures);

            return post;
        }
    }
}
