package com.example.tracky.user;

import com.example.tracky._core.enums.GenderEnum;
import lombok.Data;

public class UserRequest {

    @Data
    public static class UpdateDTO {
        private String username; // 유저 이름
        private String email; // 유저 이메일
        private String profileUrl; // 프로필 이미지 주소
        private Double height; // 177.5(cm)
        private Double weight; // 75.5(kg)
        private GenderEnum gender; // (남 | 여)
        private String location; // 활동지
        private String letter; // 자기소개
    }

    @Data
    public static class FCMDTO {
        private String fcmToken; // fcm 토큰
    }
}
