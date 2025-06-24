package com.example.tracky._core.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormatDate {

    /**
     * 현재 서버 시간대 id
     * <p>
     * Asia/Seoul
     */
    static ZoneId zoneId = ZoneId.systemDefault();

    /**
     * 날짜를 '오늘', '어제' 또는 'yyyy년 MM월 dd일' 형식으로 변환합니다.
     */
    public static String formatDateRelatively(Timestamp timestamp) {
        ZonedDateTime zonedDateTime = timestamp.toInstant().atZone(zoneId); // 서울 시간대로 변경
        LocalDate today = LocalDate.now(zoneId);
        LocalDate inputDate = zonedDateTime.toLocalDate();

        if (inputDate.isEqual(today)) {
            return "오늘";
        } else if (inputDate.isEqual(today.minusDays(1))) {
            return "어제";
        } else {
            // '오늘'과 '어제'가 아니면 전체 날짜 형식을 반환합니다.
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy. M. dd.", Locale.KOREAN);
            return zonedDateTime.format(dateFormatter);
        }
    }

    /**
     * 시간을 'HH:mm' 형식으로 변환합니다.
     */
    public static String formatTime(Timestamp timestamp) {
        ZonedDateTime zonedDateTime = timestamp.toInstant().atZone(zoneId); // 서울 시간대로 변경
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return zonedDateTime.format(timeFormatter);
    }

}
