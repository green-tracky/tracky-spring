package com.example.tracky.community.challenge.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ChallengeStatus {
    INPROGRESS("진행중"),
    COMPLETED("종료");

    private final String value;

    /**
     * 주어진 문자열(label)에 해당하는 ChallengeStatus enum 값을 반환합니다.
     *
     * - Enum의 모든 값을 스트림으로 변환한 후
     * - label 값이 일치하는 항목을 필터링하고
     * - 일치하는 첫 번째 항목을 반환합니다.
     * - 일치하는 값이 없으면 IllegalArgumentException 예외를 던집니다.
     *
     * @param value 매칭할 문자열 라벨
     * @return 일치하는 ChallengeStatus enum 값
     * @throws IllegalArgumentException 일치하는 label이 없을 경우
     */
    public static ChallengeStatus fromValue(String value) {
        return Arrays.stream(ChallengeStatus.values())
                .filter(status -> status.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + value));
    }
}
