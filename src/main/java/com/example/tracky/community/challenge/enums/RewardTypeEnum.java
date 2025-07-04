package com.example.tracky.community.challenge.enums;

import com.example.tracky._core.error.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum RewardTypeEnum {

    GOLD("금메달"),
    SILVER("은메달"),
    BRONZE("동메달"),
    PARTICIPATION("참여상");

    // DB에 저장될 고유한 한글 이름
    private final String value;

    /**
     * json -> DTO 역직렬화 될때 사용됨. DTO 용
     *
     * @param value DB의 "금메달" 또는 "은메달"
     * @return RewardTypeEnum.GOLD 또는 RewardTypeEnum.SILVER
     * @JsonCreator JSON의 특정 값(여기서는 "도로" 같은 문자열)으로 Java 객체(여기서는 RewardTypeEnum)를 만드는 방법을 Jackson(Spring의 기본 JSON 라이브러리)에게 알려줄 수 있습니다.
     */
    @JsonCreator
    public static RewardTypeEnum fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Stream.of(RewardTypeEnum.values())
                .filter(type -> type.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new ExceptionApi400(ErrorCodeEnum.INVALID_REWARD_TYPE));
    }

    /**
     * Jackson이 이 Enum을 JSON으로 변환할 때 이 메서드의 반환값을 사용하도록 지정합니다.
     *
     * @return 데이터베이스에 저장될 한글 문자열 값 (예: "일반")
     */
    @JsonValue
    public String getValue() {
        return value;
    }

}
