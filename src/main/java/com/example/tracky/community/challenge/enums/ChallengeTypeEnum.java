package com.example.tracky.community.challenge.enums;

import com.example.tracky._core.error.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum ChallengeTypeEnum {

    PUBLIC("공개"),
    PRIVATE("사설");

    private final String value;

    /**
     * json -> DTO 역직렬화 될때 사용됨. DTO 용
     *
     * @param value DB의 "금메달" 또는 "은메달"
     * @return ChallengeTypeEnum.PUBLIC 또는 ChallengeTypeEnum.PRIVATE
     * @JsonCreator JSON의 특정 값(여기서는 "도로" 같은 문자열)으로 Java 객체(여기서는 ChallengeTypeEnum)를 만드는 방법을 Jackson(Spring의 기본 JSON 라이브러리)에게 알려줄 수 있습니다.
     */
    @JsonCreator
    public static ChallengeTypeEnum fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Stream.of(ChallengeTypeEnum.values())
                .filter(type -> type.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new ExceptionApi400(ErrorCodeEnum.INVALID_CHALLENGE_TYPE));
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

    // [핵심] @DiscriminatorValue에서 사용할 문자열 상수 정의
    // @DiscriminatorValue(ChallengeTypeEnum.PRIVATE_TYPE) 여기엔 상수 문자열만 들어간다 따라서 이렇게 사용해야한다
    // 이렇게 하면 "PUBLIC", "PRIVATE"이라는 문자열을 중앙에서 관리할 수 있고,
    // 오타가 발생할 위험이 사라집니다.
    // 딱 문자열 상수가 필요하기 때문에 사용하는 것
    // 로직에 필요한게 아니기 때문에 한글로 저장하지 않는다
    public static final String PUBLIC_TYPE = "PUBLIC";
    public static final String PRIVATE_TYPE = "PRIVATE";
}