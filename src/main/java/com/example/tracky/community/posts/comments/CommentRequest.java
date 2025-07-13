package com.example.tracky.community.posts.comments;

import com.example.tracky.community.posts.Post;
import com.example.tracky.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class CommentRequest {

    @Data
    public static class SaveDTO {

        private Integer postId;
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        @Size(min = 1, max = 300, message = "댓글은 1자 이상 300자 이하여야 합니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\s.,!?\"'()\\-]*$",
                message = "댓글에 허용되지 않은 특수문자가 포함되어 있습니다."
        )
        private String content;
        private Integer parentId;

        public Comment toEntity(User user, Post post, Comment parent) {
            Comment comment = Comment.builder()
                    .content(content)
                    .user(user)
                    .post(post)
                    .parent(parent)
                    .build();
            return comment;
        }
    }

    @Data
    public static class UpdateDTO {

        private Integer postId;
        private String content;
        private Integer parentId;

    }

}
