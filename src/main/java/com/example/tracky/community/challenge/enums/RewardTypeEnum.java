package com.example.tracky.community.challenge.enums;

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
    private final String koreanName;

    /**
     * 한글 이름을 기반으로 해당하는 Enum 상수를 찾아 반환하는 정적 메소드.
     * 컨버터에서 이 메소드를 사용하여 DB 값을 다시 Enum 객체로 변환합니다.
     */
    public static RewardTypeEnum fromKoreanName(String koreanName) {
        return Stream.of(RewardTypeEnum.values())
                .filter(type -> type.getKoreanName().equals(koreanName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리워드 타입입니다: " + koreanName));
    }
}
