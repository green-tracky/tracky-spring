package com.example.tracky._core.value;

import java.time.LocalDateTime;

public class TimeValue {

    /**
     * LocalDateTime.now()
     *
     * @return
     */
    private static LocalDateTime getRealTime() {
        return LocalDateTime.now();
    }

    /**
     * LocalDateTime.of(2025, 6, 23, 00, 00, 00)
     *
     * @return
     */
    private static LocalDateTime getDevTime() {
        return LocalDateTime.of(2025, 6, 23, 00, 00, 00);
    }

    /**
     * 서버 시간 설정
     *
     * @return
     */
    public static LocalDateTime getServerTime() {
        return getDevTime();
    }
}
