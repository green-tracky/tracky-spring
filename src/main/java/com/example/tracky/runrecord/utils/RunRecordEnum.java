package com.example.tracky.runrecord.utils;

import java.time.LocalDate;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class RunRecordEnum {

    @Getter
    @RequiredArgsConstructor
    public static enum RunPlaceEnum {
        ROAD("도로"),
        TRACK("트랙"),
        TRAIL("산길");

        private final String name;
    }

    // 사용자가 선택한 기간 타입
    public static enum PeriodType {
        WEEK, MONTH, YEAR, ALL
    }

    @Data
    public static class PeriodRequest {
        private PeriodType periodType;
        private LocalDate baseDate;
    }

}
