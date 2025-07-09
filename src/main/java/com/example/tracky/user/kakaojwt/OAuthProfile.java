package com.example.tracky.user.kakaojwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * idToken 페이로드 저장소
 */
@Data
public class OAuthProfile {
    private String aud; // 앱 키
    private String sub; // 고유 사용자 ID

    @JsonProperty("auth_time")
    private Long authTime; // 인증 시간 (Epoch 초)

    private String iss; // 발급자 (issuer)
    private String nickname; // 사용자 닉네임

    private Long exp; // 만료 시간 (Epoch 초)
    private Long iat; // 발급 시간 (Epoch 초)

    private String picture; // 프로필 사진 URL
}