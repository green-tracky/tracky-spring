package com.example.tracky.runrecord.utils;

import org.junit.jupiter.api.Test;

public class RunRecordUtilTest {

    @Test
    public void calculatePace_test() {
        int distanceMeters = 100;
        int durationSeconds = 15;
        String pace = RunRecordUtil.calculatePace(distanceMeters, durationSeconds);

        System.out.println(pace);
    }

    @Test
    public void calculateCalories_test() {
        int distanceMeters = 100;
        int durationSeconds = 15;
        double weightKg = 70.0;

        int calories = RunRecordUtil.calculateCalories(distanceMeters, durationSeconds, weightKg);

        System.out.println(calories + "칼로리");
    }
}
