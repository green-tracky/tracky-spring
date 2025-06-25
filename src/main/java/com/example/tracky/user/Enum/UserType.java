package com.example.tracky.user.Enum;

import com.example.tracky._core.error.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum UserType {
    GENERAL("일반"),
    ADMIN("관리자");

    private final String value;

    /**
     * DB에서 읽어온 값(String)으로 적절한 Enum 상수(Type)를 찾습니다.
     *
     * @param value DB의 "일반" 또는 "관리자"
     * @return Type.ADMIN 또는 Type.GENERAL
     */
    public static UserType fromDbValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Stream.of(UserType.values())
                .filter(userType -> userType.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new ExceptionApi400(ErrorCodeEnum.INVALID_USER_TYPE));
    }
}
