package com.example.tracky.runrecord.utils;

@Deprecated
public class RunRecordUtil {
    /**
     * 미터(m) 단위의 거리와 초(s) 단위의 시간을 입력받아
     * <p>
     * 사용자에게 보여줄 페이스(분/km) 문자열을 반환합니다.
     *
     * @param distanceMeters  달린 거리 (미터 단위)
     * @param durationSeconds 달린 시간 (초 단위)
     * @return 포맷팅된 페이스 문자열 (예: "6분 0초/km")
     */
    public static String calculatePace(int distanceMeters, int durationSeconds) {
        // 거리가 0 이하일 경우 계산이 불가능하므로 예외 처리
        if (distanceMeters <= 0) {
            return "0분 0초/km";
        }

        // 1. 1km를 가는 데 걸리는 시간을 초 단위로 계산합니다. (핵심 변환)
        // 정수 나눗셈으로 인한 오차를 방지하기 위해 double로 형변환 후 계산합니다.
        // (totalSeconds / totalMeters) * 1000 계산식
        double paceInSecondsPerKm = ((double) durationSeconds / distanceMeters) * 1000.0;

        // 2. 계산된 초를 '분'과 '초'로 분리합니다.
        int minutes = (int) paceInSecondsPerKm / 60;
        int seconds = (int) paceInSecondsPerKm % 60;

        // 3. 최종 문자열로 포맷팅하여 반환합니다.
        return String.format("%d분 %d초/km", minutes, seconds);
    }

    /**
     * 달리기 기록(거리, 시간)과 체중을 기반으로 소모 칼로리를 계산합니다.
     * <p>
     * 내부적으로 평균 속도를 계산하여 적절한 MET 값을 찾아 적용합니다.
     *
     * @param distanceMeters  이동 거리 (미터)
     * @param durationSeconds 소요 시간 (초)
     * @param weightKg        체중 (kg)
     * @return 계산된 소모 칼로리 (kcal)
     */
    public static int calculateCalories(int distanceMeters, int durationSeconds, double weightKg) {
        if (distanceMeters <= 0 || durationSeconds <= 0 || weightKg <= 0) {
            return 0;
        }

        // 1. 평균 속도 계산 (km/h)
        double distanceKm = distanceMeters / 1000.0;
        double durationHours = durationSeconds / 3600.0;
        double averageSpeedKmh = distanceKm / durationHours;

        // 2. 속도에 맞는 MET 값 결정
        // 또는 회귀식 사용 (더 정밀함)
        double met = (1.348 * averageSpeedKmh) + 0.52;

        // 3. 초 -> 분
        double durationMinutes = durationSeconds / 60.0;

        // 4. 먼저 double 타입으로 칼로리 계산을 완료합니다.
        // '1분당 소모하는 칼로리(kcal/min)'를 계산
        double caloriesAsDouble = (met * 3.5 * weightKg / 200) * durationMinutes;

        // 5. Math.round()를 사용해 소수점 첫째 자리에서 반올림합니다.
        // 그 결과를 int로 형변환하여 반환합니다.
        return (int) Math.round(caloriesAsDouble);
    }

    public static int calculateTotalDistanceMeters() {
    }
}
