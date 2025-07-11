package com.example.tracky._core.enums;

import com.example.tracky._core.error.ex.ExceptionApi400;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NotificationTypeEnum {
    FRIEND_INVITE("friendInvite"),
    CHALLENGE_INVITE("challengeInvite");

    private final String displayName;

    /**
     * DB -> Enum 변환
     *
     * @param value DB의 "PUBLIC" 또는 "PRIVATE"
     * @return ChallengeTypeEnum.PUBLIC 또는 ChallengeTypeEnum.PRIVATE
     * @JsonCreator JSON의 특정 값(여기서는 "도로" 같은 문자열)으로 Java 객체(여기서는 ChallengeTypeEnum)를 만드는 방법을 Jackson(Spring의 기본 JSON 라이브러리)에게 알려줄 수 있습니다.
     */
    @JsonCreator
    public static NotificationTypeEnum fromValue(String value) {
        if (value == null || value.isEmpty()) return null;
        // 한글 매칭
        for (NotificationTypeEnum type : values()) {
            if (type.displayName.equals(value)) return type;
        }
        // 영문 매칭 (대소문자 구분 없이)
        try {
            return NotificationTypeEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ExceptionApi400(ErrorCodeEnum.INVALID_CHALLENGE_TYPE);
        }
    }

    /**
     * Jackson이 이 Enum을 JSON으로 변환할 때 이 메서드의 반환값을 사용하도록 지정합니다.
     *
     * @return 데이터베이스에 저장될 한글 문자열 값 (예: "일반")
     */
    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

}