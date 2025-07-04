package com.example.tracky.runrecord.utils;

import com.example.tracky.runrecord.runsegments.RunSegmentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class RunRecordUtilTest {

    @Test
    @DisplayName("페이스 계산 - 1km를 6분에 달렸을 경우")
    void calculate_pace_test() {
        // given - 1000m를 360초(6분)에 달린 상황이 주어짐
        int distanceMeters = 1000;
        int durationSeconds = 360;

        // when - 페이스를 계산하면
        int pace = RunRecordUtil.calculatePace(distanceMeters, durationSeconds);

        // eye
        System.out.println("✅페이스: " + pace);

    }

    @Test
    @DisplayName("칼로리 계산 - 5km를 30분 달린 70kg 사용자의 경우")
    void calculate_calories_test() {
        // given - 5km를 30분 달린 70kg 사용자의 데이터가 주어짐
        int distanceMeters = 5000;
        int durationSeconds = 1800;
        double weightKg = 70.0;
        int expectedCalories = 515;

        // when - 소모 칼로리를 계산하면
        int calories = RunRecordUtil.calculateCalories(distanceMeters, durationSeconds, weightKg);

        // eye
        System.out.println("✅칼로리: " + calories);
    }

    @Test
    @DisplayName("구간 누적 거리 계산 - 여러 구간이 주어졌을 경우")
    void calculate_total_distance_meters_test() {
        // given - 여러 개의 달리기 구간 데이터가 주어짐
        RunSegmentRequest.DTO dto1 = new RunSegmentRequest.DTO();
        dto1.setDistanceMeters(300);
        RunSegmentRequest.DTO dto2 = new RunSegmentRequest.DTO();
        dto2.setDistanceMeters(800);
        RunSegmentRequest.DTO dto3 = new RunSegmentRequest.DTO();
        dto3.setDistanceMeters(100);

        List<RunSegmentRequest.DTO> segments = Arrays.asList(dto1, dto2, dto3);
        int expectedTotalDistance = 1200;

        // when - 총 거리를 계산하면
        int totalDistance = RunRecordUtil.calculateTotalDistanceMeters(segments);

        // eye
        System.out.println("✅총 이동거리: " + totalDistance);
    }

    @Test
    @DisplayName("구간 누적 시간 계산 - 여러 구간이 주어졌을 경우")
    void calculate_total_duration_seconds_test() {
        // given - 여러 개의 달리기 구간 데이터가 주어짐
        RunSegmentRequest.DTO dto1 = new RunSegmentRequest.DTO();
        dto1.setDurationSeconds(600);
        RunSegmentRequest.DTO dto2 = new RunSegmentRequest.DTO();
        dto2.setDurationSeconds(300);
        RunSegmentRequest.DTO dto3 = new RunSegmentRequest.DTO();
        dto3.setDurationSeconds(100);

        List<RunSegmentRequest.DTO> segments = Arrays.asList(dto1, dto2, dto3);
        int expectedTotalDuration = 1000;

        // when - 총 시간을 계산하면
        int totalDuration = RunRecordUtil.calculateTotalDurationSeconds(segments);

        // eye
        System.out.println("✅총 누적거리: " + totalDuration);
    }

}
