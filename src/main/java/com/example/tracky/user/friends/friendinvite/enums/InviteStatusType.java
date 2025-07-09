package com.example.tracky.user.friends.friendinvite.enums;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InviteStatusType {
    WAITING("대기중"),
    ACCEPTED("수락됨"),
    REJECTED("거절됨");

    private final String displayName;

    /**
     * DB -> Enum 변환
     *
     * @param value DB의 "PUBLIC" 또는 "PRIVATE"
     * @return ChallengeTypeEnum.PUBLIC 또는 ChallengeTypeEnum.PRIVATE
     * @JsonCreator JSON의 특정 값(여기서는 "도로" 같은 문자열)으로 Java 객체(여기서는 ChallengeTypeEnum)를 만드는 방법을 Jackson(Spring의 기본 JSON 라이브러리)에게 알려줄 수 있습니다.
     */
    @JsonCreator
    public static InviteStatusType fromValue(String value) {
        if (value == null || value.isEmpty()) return null;
        // 한글 매칭
        for (InviteStatusType type : values()) {
            if (type.displayName.equals(value)) return type;
        }
        // 영문 매칭 (대소문자 구분 없이)
        try {
            return InviteStatusType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ExceptionApi400(ErrorCodeEnum.INVALID_INVITE_STATUS);
        }
    }


    InviteStatusType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}
