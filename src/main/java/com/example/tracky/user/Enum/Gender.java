package com.example.tracky.user.Enum;

import com.example.tracky._core.error.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("남"),
    FEMALE("여");

    private final String value; // DB에 저장될 실제 값 ("남", "여")

    /**
     * DB에서 읽어온 값(String)으로 적절한 Enum 상수(Gender)를 찾습니다.
     *
     * @param value DB의 "남" 또는 "여"
     * @return Gender.MALE 또는 Gender.FEMALE
     */
    public static Gender fromDbValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Stream.of(Gender.values())
                .filter(gender -> gender.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new ExceptionApi400(ErrorCodeEnum.INVALID_GENDER));
    }
}