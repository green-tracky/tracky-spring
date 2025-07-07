package com.example.tracky.user;

import lombok.Data;

public class UserResponse {

    @Data
    public static class PostUserDTO {
        private Integer id;
        private String username;
        private String profileUrl;

        public PostUserDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.profileUrl = user.getProfileUrl();
        }


    }
}
