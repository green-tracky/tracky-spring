package com.example.tracky.community.challenges.utils;

import com.example.tracky._core.values.TimeValue;

import java.time.Duration;
import java.time.LocalDateTime;

public class ChallengeUtil {

    /**
     * 남은 시간 초단위로 알려줌
     *
     * @param endDate
     * @return
     */
    public static Integer calculateRemainingSeconds(LocalDateTime endDate) {
        Duration duration = Duration.between(TimeValue.getServerTime(), endDate);
        return duration.isNegative() ? 0 : (int) duration.getSeconds();
    }
}
