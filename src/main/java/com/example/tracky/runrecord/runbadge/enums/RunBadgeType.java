package com.example.tracky.runrecord.runbadge.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

/**
 * 뱃지의 성격과 획득 규칙을 구분하는 타입을 정의하는 Enum 클래스입니다.
 * 이 Enum을 사용하면 서비스 로직에서 타입에 따라 분기 처리를 명확하고 안전하게 할 수 있습니다.
 * 각 Enum 상수는 데이터베이스에 저장될 한글 값을 필드로 가집니다.
 */
@RequiredArgsConstructor
public enum RunBadgeType {

    /**
     * '최고 기록' 뱃지 타입입니다.
     * 사용자당 1개만 소유 가능하며, 더 좋은 기록이 나오면 갱신(이전)됩니다.
     * 예: '1K 최고 기록', '5K 최고 기록'
     */
    RECORD("최고기록"),

    /**
     * <pre>
     * '월간 업적' 뱃지 타입입니다.
     * 매월 특정 조건을 만족하면 반복적으로 획득할 수 있습니다.
     * 예: '첫 시작' 뱃지 (매달 첫 번째 러닝에 부여)
     * 기존 누적과 일반 을 합침------
     * '월간 누적 업적' 뱃지 타입입니다.
     *  매월 누적된 기록을 바탕으로 판단하며, 해당 월에 한 번만 획득 가능합니다.
     *  예: '브론즈', '실버' 뱃지 (해당 월의 누적 거리로 판단)
     *  </pre>
     */
    MONTHLY_ACHIEVEMENT("월간업적");

    /**
     * 데이터베이스에 저장될 한글 문자열 값입니다.
     * 예: RECORD -> "최고 기록"
     */
    private final String value;

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
