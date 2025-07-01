package com.example.tracky.community.challenge.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeTypeEnum {
    // Enum 상수 정의
    // 아직을 쓸대가 없음
    PUBLIC("공개"),
    PRIVATE("사설");

    private final String value;

    // [핵심] @DiscriminatorValue에서 사용할 문자열 상수 정의
    // @DiscriminatorValue(ChallengeTypeEnum.PRIVATE_TYPE) 여기엔 상수 문자열만 들어간다 따라서 이렇게 사용해야한다
    // 이렇게 하면 "PUBLIC", "PRIVATE"이라는 문자열을 중앙에서 관리할 수 있고,
    // 오타가 발생할 위험이 사라집니다.
    // 딱 문자열 상수가 필요하기 때문에 사용하는 것
    // 로직에 필요한게 아니기 때문에 한글로 저장하지 않는다
    public static final String PUBLIC_TYPE = "PUBLIC";
    public static final String PRIVATE_TYPE = "PRIVATE";
}