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
        private Integer id; // 유저 db 아이디
        private String username; // 유저 이름 토큰에서 가져옴
        private String profileUrl; // 유저 프로필 이미지 토큰에서 가져옴
        private String loginId; // 로그인시 식별할 아이디 토큰의 정보로 만듦
        private ProviderTypeEnum provider; // OIDC 제공회사

        public IdTokenDTO(User user, String idToken) {
            this.idToken = idToken;
            this.id = user.getId();
            this.username = user.getUsername();
            this.profileUrl = user.getProfileUrl();
            this.loginId = user.getLoginId();
            this.provider = user.getProvider();
        }
    }

}
