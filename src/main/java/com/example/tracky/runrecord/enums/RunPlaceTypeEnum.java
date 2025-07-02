package com.example.tracky.runrecord.enums;

import com.example.tracky._core.error.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum RunPlaceTypeEnum {

    ROAD("도로"),
    TRACK("트랙"),
    TRAIL("산길");

    private final String value;

    /**
     * DB에서 읽어온 값(String)으로 적절한 Enum 상수(RunPlace)를 찾습니다.
     *
     * @param value DB의 "도로" 또는 "트랙"
     * @return RunPlace.ROAD 또는 RunPlace.TRACK
     * @JsonCreator JSON의 특정 값(여기서는 "도로" 같은 문자열)으로 Java 객체(여기서는 RunPlaceEnum)를 만드는 방법을 Jackson(Spring의 기본 JSON 라이브러리)에게 알려줄 수 있습니다.
     */
    @JsonCreator
    public static RunPlaceTypeEnum fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Stream.of(RunPlaceTypeEnum.values())
                .filter(runPlaceEnum -> runPlaceEnum.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new ExceptionApi400(ErrorCodeEnum.INVALID_RUN_PLACE_TYPE));
    }

    /**
     * Jackson이 이 Enum을 JSON으로 변환할 때 이 메서드의 반환값을 사용하도록 지정합니다.
     *
     * @return 데이터베이스에 저장될 한글 문자열 값 (예: "최고기록")
     */
    @JsonValue
    public String getValue() {
        return value;
    }

}
