package com.example.tracky.user.utils;

import com.example.tracky.user.kakaojwt.OAuthProfile;

public class LoginIdUtil {

    /**
     * 주어진 URL에서 도메인(예: kakao)만 추출해서 대문자로 반환
     */
    public static String extractProvider(String url) {
        if (url == null || url.isBlank()) return null;

        // "https://kauth.kakao.com" → "kauth.kakao.com"
        String noProtocol = url.replaceFirst("https?://", "");

        String[] parts = noProtocol.split("\\.");
        if (parts.length >= 2) {
            return parts[1].toUpperCase(); // 예: "kakao" → "KAKAO"
        }
        return null;
    }

    /**
     * OAuthProfile 을 가지고 loginId 를 반환
     *
     * @param sessionProfile
     * @return
     */
    public static String makeLoginId(OAuthProfile sessionProfile) {
        String iss = sessionProfile.getIss();

        // KAKAO_128937198372
        return extractProvider(iss) + "_" + sessionProfile.getSub();
    }
}
