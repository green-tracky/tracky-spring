package com.example.tracky.runrecord;

import lombok.Data;

public class RunRecordResponse {

    @Data
    public static class DTO {
        private Integer id;
        private String title; // 러닝 제목
        private Double distance; // 러닝 총 거리. km 단위
        private Integer elapsedTime; // 러닝 총 시간. 초 단위
        private Integer calories; // 총 칼로리 소모량
    }
}
