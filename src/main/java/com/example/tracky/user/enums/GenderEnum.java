package com.example.tracky.user.enums;

import com.example.tracky._core.error.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum GenderEnum {
    MALE("남"),
    FEMALE("여");

    private final String value; // DB에 저장될 실제 값 ("남", "여")

    /**
     * json -> DTO 역직렬화 될때 사용됨. DTO 용
     *
     * @param value DB의 "남" 또는 "여"
     * @return GenderEnum.MALE 또는 GenderEnum.FEMALE
     * @JsonCreator JSON의 특정 값(여기서는 "도로" 같은 문자열)으로 Java 객체(여기서는 GenderEnum)를 만드는 방법을 Jackson(Spring의 기본 JSON 라이브러리)에게 알려줄 수 있습니다.
     */
    @JsonCreator
    public static GenderEnum fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Stream.of(GenderEnum.values())
                .filter(genderEnum -> genderEnum.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new ExceptionApi400(ErrorCodeEnum.INVALID_GENDER));
    }

    /**
     * Jackson이 이 Enum을 JSON으로 변환할 때 이 메서드의 반환값을 사용하도록 지정합니다.
     *
     * @return 데이터베이스에 저장될 한글 문자열 값 (예: "남")
     */
    @JsonValue
    public String getValue() {
        return value;
    }
}