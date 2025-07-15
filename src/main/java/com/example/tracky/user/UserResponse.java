package com.example.tracky.user;

import com.example.tracky._core.enums.GenderEnum;
import com.example.tracky._core.enums.ProviderTypeEnum;
import com.example.tracky.user.runlevel.RunLevelResponse;
import lombok.Data;

import java.time.LocalDateTime;

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
        private UserDTO user;

        public IdTokenDTO(User user, String idToken) {
            this.idToken = idToken;
            this.user = new UserDTO(user);
        }


    }

    @Data
    public static class UserDTO {
        private Integer id;
        private String loginId; // 로그인용 및 사용자 조회용 아이디
        private String username; // 유저 이름
        private String profileUrl; // 프로필 이미지 주소
        private Double height; // 177.5(cm)
        private Double weight; // 75.5(kg)
        private GenderEnum gender; // (남 | 여)
        private String location; // 활동지
        private String letter; // 자기소개
        private ProviderTypeEnum provider; // oauth 제공자 (kakao, google)
        private String userTag; // #UUID 6자리
        private String fcmToken; // 기기 식별 아이디 // 알림서비스용
        private LocalDateTime createdAt;

        public UserDTO(User user) {
            this.id = user.getId();
            this.loginId = user.getLoginId();
            this.username = user.getUsername();
            this.profileUrl = user.getProfileUrl();
            this.height = user.getHeight();
            this.weight = user.getWeight();
            this.gender = user.getGender();
            this.location = user.getLocation();
            this.letter = user.getLetter();
            this.provider = user.getProvider();
            this.userTag = user.getUserTag();
            this.fcmToken = user.getFcmToken();
            this.createdAt = user.getCreatedAt();
        }
    }

    @Data
    public static class UpdateDTO {
        private Integer id;
        private String username;
        private String profileUrl;
        private Double height;
        private Double weight;
        private GenderEnum gender;
        private String location;
        private String letter;
        private LocalDateTime updatedAt;

        public UpdateDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.profileUrl = user.getProfileUrl();
            this.height = user.getHeight();
            this.weight = user.getWeight();
            this.gender = user.getGender();
            this.location = user.getLocation();
            this.letter = user.getLetter();
            this.updatedAt = user.getUpdatedAt();
        }
    }

    @Data
    public static class DetailDTO {
        private Integer id;
        private String loginId;
        private String username;
        private String profileUrl;
        private Double height;
        private Double weight;
        private GenderEnum gender;
        private String location;
        private String letter;
        private String userTag;
        private String fcmToken;
        private RunLevelResponse.DTO runLevel;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean isOwner;

        public DetailDTO(User user, String loginId) {
            this.id = user.getId();
            this.loginId = user.getLoginId();
            this.username = user.getUsername();
            this.profileUrl = user.getProfileUrl();
            this.height = user.getHeight();
            this.weight = user.getWeight();
            this.gender = user.getGender();
            this.location = user.getLocation();
            this.letter = user.getLetter();
            this.userTag = user.getUserTag();
            this.fcmToken = user.getFcmToken();
            this.runLevel = new RunLevelResponse.DTO(user.getRunLevel());
            this.createdAt = user.getCreatedAt();
            this.updatedAt = user.getUpdatedAt();
            this.isOwner = user.getLoginId().equals(loginId);
        }

    }

}
