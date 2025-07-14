package com.example.tracky.community.posts.comments;

import com.example.tracky._core.constants.Constants;
import com.example.tracky.community.posts.Post;
import com.example.tracky.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class CommentRequest {

    @Data
    public static class SaveDTO {
        @Positive(message = "유효한 부모 댓글 ID가 아닙니다.")
        private Integer parentId;

        @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
        @Size(max = Constants.CONTENT_LENGTH, message = "댓글 내용은 " + Constants.CONTENT_LENGTH + "자를 초과할 수 없습니다.")
        private String content;

        public Comment toEntity(User user, Post post, Comment parent) {
            return Comment.builder()
                    .content(content)
                    .user(user)
                    .post(post)
                    .parent(parent)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {

        @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
        @Size(max = Constants.CONTENT_LENGTH, message = "댓글 내용은 " + Constants.CONTENT_LENGTH + "자를 초과할 수 없습니다.")
        private String content;
    }
}
