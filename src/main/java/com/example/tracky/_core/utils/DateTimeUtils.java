package com.example.tracky._core.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 날짜 및 시간 관련 유틸리티 클래스
 */
@Deprecated
public class DateTimeUtils {

    /**
     * Timestamp를 서버의 기본 시간대를 기준으로 'yyyy-MM-dd HH:mm:ss' 형식의 문자열로 변환합니다.
     * <p>
     * [주의] 이 메서드는 실행 환경의 기본 시간대에 따라 결과가 달라질 수 있습니다.
     *
     * @param timestamp 변환할 Timestamp 객체
     * @return 포맷팅된 날짜/시간 문자열
     */
    public static String toDateTimeString(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }

        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return localDateTime.format(formatter);
    }
}

