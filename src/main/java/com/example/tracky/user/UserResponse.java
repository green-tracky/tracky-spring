package com.example.tracky.user;

import com.example.tracky._core.enums.ProviderTypeEnum;
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

    @Data
    public static class IdTokenDTO {
        private String idToken;
        private Integer id; // 유저 아이디
        private String username; // 유저 이름
        private ProviderTypeEnum provider;

        public IdTokenDTO(User user, String idToken) {
            this.idToken = idToken;
            this.id = user.getId();
            this.username = user.getUsername();
        }

    }

}
