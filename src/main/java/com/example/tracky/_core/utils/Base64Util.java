package com.example.tracky._core.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {

    public static String decodeBase64(String base64Encoded) {
        // 원본 Base64 문자열 (이스케이프된 \n 포함)
        String rawFbPrivateKey = base64Encoded;

        // 1. [핵심] 정규식을 사용하여 모든 공백 문자(\\s)와 이스케이프된 \\n을 제거합니다.
        //    먼저 이스케이프된 백슬래시를 제거하고, 그 다음 모든 공백을 제거합니다.
        String cleanBase64String = rawFbPrivateKey.replace("\\n", "").replaceAll("\\s", "");

        // 2. 깨끗해진 순수 Base64 문자열을 디코딩합니다.
        byte[] decodedBytes = Base64.getDecoder().decode(cleanBase64String);

        // 3. 디코딩된 바이트 배열을 UTF-8 문자열로 변환합니다.
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        return decodedString;
    }
}
