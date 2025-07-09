package com.example.tracky.community.posts.comments;

import com.example.tracky.community.posts.Post;
import com.example.tracky.user.User;
import lombok.Data;

public class CommentRequest {

    @Data
    public static class SaveDTO {

        private final Integer postId;
        private final String username;
        private final String content;
        private final Integer parentId;


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
}
