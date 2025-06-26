package com.example.tracky.runrecord.runbadge.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 뱃지의 타입을 정의하는 Enum 클래스입니다.
 * DB에 저장될 한글 값을 필드로 가집니다.
 */
@Getter
@RequiredArgsConstructor
public enum RunBadgeType {
    ACHIEVEMENT("업적"), // 업적 타입 (한 번만 획득)
    RECORD("기록");      // 기록 타입 (개인 최고 기록처럼 갱신 가능)

    private final String value;
}
