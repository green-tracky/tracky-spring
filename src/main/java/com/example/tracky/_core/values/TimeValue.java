package com.example.tracky._core.values;

import java.time.LocalDateTime;

public class TimeValue {

    // 1. 테스트 시간을 저장할 정적 변수 추가
    private static LocalDateTime testTime = null;

    private static LocalDateTime getRealTime() {
        return LocalDateTime.now();
    }

    private static LocalDateTime getDevTime() {
        return LocalDateTime.of(2025, 6, 23, 0, 0, 0);
    }

    /**
     * 서버의 기준 시간을 반환합니다.
     * 테스트 시간이 설정되어 있으면 그 시간을, 아니면 환경에 맞는 시간을 반환합니다.
     */
    public static LocalDateTime getServerTime() {
        // 2. 테스트 시간이 설정되어 있으면, 최우선으로 테스트 시간을 반환
        if (testTime != null) {
            return testTime;
        }

        // 테스트 시간이 없으면, 기존 로직에 따라 개발용 또는 실제 시간을 반환
        // 이 부분을 개발/운영 환경에 맞게 선택하여 사용합니다.
        // return getDevTime();      // 개발 시
        return getRealTime();  // 운영(배포) 시
    }

    /**
     * 테스트용 시간을 설정하는 메서드.
     * 테스트 코드의 @BeforeEach 등에서 호출하여 시간을 고정시킬 수 있습니다.
     *
     * @param time 고정할 시간
     */
    public static void setTestTime(LocalDateTime time) {
        testTime = time;
    }

    /**
     * 설정된 테스트 시간을 초기화하는 메서드.
     * 각각의 테스트가 서로 영향을 주지 않도록 @AfterEach에서 호출하는 것이 좋습니다.
     */
    public static void clearTestTime() {
        testTime = null;
    }
}
