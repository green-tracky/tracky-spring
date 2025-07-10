package com.example.tracky.user.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UserTagUtil {

    /**
     * 기존 태그 리스트와 비교하여 중복되지 않는 고유한 유저 태그를 생성합니다.
     *
     * @param existingTags DB에서 조회한 모든 유저 태그 리스트
     * @return 중복되지 않는 새로운 유저 태그 (예: "#A3B123")
     */
    public static String generateUniqueUserTag(List<String> existingTags) {
        Set<String> tagSet = new HashSet<>(existingTags);
        String newTag;

        // 24비트는 16,777,216가지 경우의 수를 가집니다.
        // 충돌 확률이 매우 낮으므로 대부분의 경우 한 번에 생성됩니다.
        while (true) {
            UUID uuid = UUID.randomUUID();
            int hashCode = Math.abs(uuid.hashCode()); // uuid 정수로 변환, 절대값으로 변환

            // 💡 핵심 수정 부분: 24비트 마스크(0xFFFFFF) 사용
            // 0xFFFFFF는 16,777,215까지의 숫자를 표현합니다.
            String hexCode = String.format("%06X", hashCode & 0xFFFFFF);
            newTag = "#" + hexCode;

            if (!tagSet.contains(newTag)) {
                break;
            }
        }
        return newTag;
    }
}