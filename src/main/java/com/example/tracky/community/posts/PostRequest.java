package com.example.tracky.community.posts;

import com.example.tracky._core.constants.Constants;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class PostRequest {

    @Data
    public static class UpdateDTO {
        @NotBlank(message = "게시글 내용은 필수 입력 항목입니다.")
        @Size(max = Constants.CONTENT_LENGTH, message = "게시글 내용은 " + Constants.CONTENT_LENGTH + "자를 초과할 수 없습니다.")
        private String content;
    }

    @Data
    public static class SaveDTO {
        @NotBlank(message = "게시글 내용은 필수 입력 항목입니다.")
        @Size(max = Constants.CONTENT_LENGTH, message = "게시글 내용은 " + Constants.CONTENT_LENGTH + "자를 초과할 수 없습니다.")
        private String content;

        @Positive(message = "유효한 달리기 기록 ID가 아닙니다.")
        private Integer runRecordId;

        public Post toEntity(User user, RunRecord runRecord) {
            return Post.builder()
                    .content(content)
                    .user(user) // user객체 필요
                    .runRecord(runRecord)
                    .build();
        }
    }
}
