package com.example.tracky.utils;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;

import com.example.tracky._core.utils.FormatDate;

public class FormatDateTest {

    @Test
    void formatDateRelatively_2025_06_24_UTC_테스트() {
        // given
        String isoString = "2025-06-24T05:32:46.357+00:00";
        Timestamp timestamp = Timestamp.from(java.time.OffsetDateTime.parse(isoString).toInstant());

        // when
        String result = FormatDate.formatDateRelatively(timestamp);

        System.out.println(result);
    }

    @Test
    void formatTime_2025_06_24_UTC_테스트() {
        // given
        String isoString = "2025-06-24T05:32:46.357+00:00";
        Timestamp timestamp = Timestamp.from(java.time.OffsetDateTime.parse(isoString).toInstant());

        // when
        String result = FormatDate.formatTime(timestamp);

        // eye
        System.out.println(result);
    }
}
