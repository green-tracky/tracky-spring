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

    @JsonProperty("auth_time") // auth_time 이렇게 json으로 오면 authTime 여기에 맵핑해줌
    private Long authTime; // 인증 시간 (Epoch 초)

    private String iss; // 발급자 (issuer)
    private String nickname; // 사용자 닉네임

    private Long exp; // 만료 시간 (Epoch 초) - 기본 12시간
    private Long iat; // 발급 시간 (Epoch 초)

    private String picture; // 프로필 사진 URL
}