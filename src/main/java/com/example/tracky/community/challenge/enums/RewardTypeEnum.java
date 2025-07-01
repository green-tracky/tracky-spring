package com.example.tracky.community.challenge.enums;

import com.example.tracky._core.error.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum RewardTypeEnum {

    GOLD("금메달"),
    SILVER("은메달"),
    BRONZE("동메달"),
    PARTICIPATION("참여상");

    // DB에 저장될 고유한 한글 이름
    private final String value;

    /**
     * 한글 이름을 기반으로 해당하는 Enum 상수를 찾아 반환하는 정적 메소드.
     * 컨버터에서 이 메소드를 사용하여 DB 값을 다시 Enum 객체로 변환합니다.
     */
    public static RewardTypeEnum fromString(String value) {
        return Stream.of(RewardTypeEnum.values())
                .filter(type -> type.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new ExceptionApi400(ErrorCodeEnum.INVALID_REWARD_TYPE));
    }
}
