package com.example.tracky.community.challenges.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class ChallengeUtil {

    /**
     * 챌린지 이미지 대체용 문자열
     *
     * @param targetDistance
     * @return
     */
    public static String formatTitle(Integer targetDistance) {
        return targetDistance / 1000 + "K";
    }

    /**
     * 남은 시간 초단위로 알려줌
     *
     * @param endDate
     * @return
     */
    public static Integer calculateRemainingSeconds(LocalDateTime endDate) {
        Duration duration = Duration.between(LocalDateTime.now(), endDate);
        return duration.isNegative() ? 0 : (int) duration.getSeconds();
    }
}
