package com.example.tracky.user;

import com.example.tracky._core.enums.GenderEnum;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

public class UserRequest {

    @Data
    public static class UpdateDTO {
        @Size(min = 2, max = 20, message = "사용자 이름은 2자 이상 20자 이하로 설정해주세요.")
        private String username; // 유저 이름

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email; // 유저 이메일

        @URL(message = "올바른 URL 형식이 아닙니다.")
        private String profileUrl; // 프로필 이미지 주소

        @Positive(message = "키는 양수여야 합니다.")
        @Digits(integer = 3, fraction = 1, message = "키는 정수 3자리, 소수점 1자리까지 입력 가능합니다.")
        private Double height; // 177.5(cm)

        @Positive(message = "몸무게는 양수여야 합니다.")
        @Digits(integer = 3, fraction = 1, message = "몸무게는 정수 3자리, 소수점 1자리까지 입력 가능합니다.")
        private Double weight; // 75.5(kg)

        private GenderEnum gender; // (남 | 여)

        @Size(max = 100, message = "활동 지역은 100자를 초과할 수 없습니다.")
        private String location; // 활동지

        @Size(max = 500, message = "자기소개는 500자를 초과할 수 없습니다.")
        private String letter; // 자기소개
    }

    @Data
    public static class FCMDTO {
        @NotEmpty(message = "fcmToken 은 비어있을 수 없습니다.")
        private String fcmToken; // fcm 토큰
    }

    @Data
    public static class IdTokenDTO {
        @NotEmpty(message = "idToken 은 비어있을 수 없습니다.")
        private String idToken; // 카카오 idToken
    }
}
