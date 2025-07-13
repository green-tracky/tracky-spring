package com.example.tracky.community.posts;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.pictures.Picture;
import com.example.tracky.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

public class PostRequest {

    @Data
    public static class UpdateDTO {

        @NotBlank(message = "내용을 입력해주세요.")
        @Size(min = 1, max = 1000, message = "게시물 내용은 1자 이상 1000자 이하여야 합니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\s.,!?\"'()\\-]*$",
                message = "내용에 허용되지 않은 특수문자가 포함되어 있습니다."
        )
        private String content;

        private Integer runRecordId;

        private List<Integer> pictureIds;

    }

    @Data
    public static class SaveDTO {

        private String content;

        private Integer runRecordId;

        private List<Integer> pictureIds;


        public Post toEntity(User user, RunRecord runRecord, List<Picture> pictures) {
            Post post = Post.builder()
                    .content(content)
                    .user(user) // user객체 필요
                    .runRecord(runRecord)
                    .build();

            List<PostPicture> postPictures = pictures.stream()
                    .map(picture -> PostPicture.builder().post(post).picture(picture).build())
                    .toList();

            post.postPictures.addAll(postPictures);

            return post;
        }
    }
}
