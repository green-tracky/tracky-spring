package com.example.tracky.runrecord.Enum;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

public class RunPeriodTypeEnum {
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
