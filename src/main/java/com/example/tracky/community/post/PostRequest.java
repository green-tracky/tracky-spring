package com.example.tracky.community.post;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.user.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

public class PostRequest {

    @Data
    public static class UpdateDTO {
        @NotEmpty(message = "제목을 입력하세요")
        private String title;
        @NotEmpty(message = "내용을 입력하세요")
        private String content;

        private Integer runRecordId;


        public Post toEntity(User user, RunRecord runRecord) {
            return Post.builder()
                    .title(title)
                    .content(content)
                    .user(user) // user객체 필요
                    .runRecord(runRecord)
                    .build();
        }
    }

    @Data
    public static class SaveDTO {
        @NotEmpty(message = "제목을 입력하세요")
        private String title;
        @NotEmpty(message = "내용을 입력하세요")
        private String content;

        private Integer runRecordId;


        public Post toEntity(User user, RunRecord runRecord) {
            return Post.builder()
                    .title(title)
                    .content(content)
                    .user(user) // user객체 필요
                    .runRecord(runRecord)
                    .build();
        }
    }
}
