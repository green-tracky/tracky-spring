package com.example.tracky.runrecord.utils;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.dto.AvgStatsDTO;
import com.example.tracky.runrecord.runsegments.RunSegment;
import com.example.tracky.runrecord.runsegments.RunSegmentRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
public class RunRecordUtil {
    /**
     * 미터(m) 단위의 거리와 초(s) 단위의 시간을 입력받아
     * <p>
     * km 단위의 페이스를 반환함. 반환된 값은 초 단위
     *
     * @param distanceMeters  달린 거리 (미터 단위)
     * @param durationSeconds 달린 시간 (초 단위)
     * @return 초 단위 페이스 수치
     */
    public static int calculatePace(int distanceMeters, int durationSeconds) {
        // 거리가 0 이하일 경우 계산이 불가능하므로 예외 처리
        if (distanceMeters <= 0) {
            return 0;
        }

        // 1. 1km를 가는 데 걸리는 시간을 초 단위로 계산합니다. (핵심 변환)
        // (totalSeconds / totalMeters) * 1000 계산식
        int paceInSecondsPerKm = (int) (((double) durationSeconds / distanceMeters) * 1000);

        return paceInSecondsPerKm;
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

    /**
     * 구간의 누적 거리 합
     * <p>
     * 미터 단위
     *
     * @param segmentDTOs
     * @return
     */
    public static int calculateTotalDistanceMeters(List<RunSegmentRequest.DTO> segmentDTOs) {
        return segmentDTOs.stream()
                .mapToInt(s -> s.getDistanceMeters())
                .sum();
    }

    /**
     * 구간의 누적 러닝 시간
     * <p>
     * 초단위
     *
     * @param segmentDTOs
     * @return
     */
    public static int calculateTotalDurationSeconds(List<RunSegmentRequest.DTO> segmentDTOs) {
        return segmentDTOs.stream()
                .mapToInt(s -> s.getDurationSeconds())
                .sum();
    }

    /**
     * 구간의 평균 페이스
     * <p>
     * 초단위
     *
     * @param segmentDTOs
     * @return
     */
    public static int calculateAvgPace(List<RunSegmentRequest.DTO> segmentDTOs) {
        double averagePace = segmentDTOs.stream()
                .mapToInt(s -> calculatePace(s.getDistanceMeters(), s.getDurationSeconds()))
                .average()
                .orElse(0.0); // 기본값 0.0

        return (int) Math.round(averagePace);
    }

    /**
     * 구간의 최고 페이스
     * <p>
     * 초단위
     *
     * @param segmentDTOs
     * @return
     */
    public static int calculateBestPace(List<RunSegmentRequest.DTO> segmentDTOs) {
        return segmentDTOs.stream()
                .mapToInt(s -> calculatePace(s.getDistanceMeters(), s.getDurationSeconds()))
                .min()
                .orElse(0); // 빈 리스트일 경우 기본값 0
    }

    /**
     * 구간 총 경과 시간
     * <p>
     * 초단위
     *
     * @param runSegments
     * @return
     */
    public static int calculateElapsedTimeInSeconds(List<RunSegment> runSegments) {
        LocalDateTime startDate = runSegments.get(0).getStartDate();
        LocalDateTime endDate = runSegments.get(runSegments.size() - 1).getEndDate();
        return (int) ChronoUnit.SECONDS.between(startDate, endDate);
    }

    /**
     * 통계 구하는 로직
     *
     * @param runRecords
     * @param totalDistanceMeters
     * @param totalDurationSeconds
     * @return AvgStatsDTO
     */
    public static AvgStatsDTO avgStats(List<RunRecord> runRecords, Integer totalDistanceMeters, Integer totalDurationSeconds) {
        // 평균 페이스 구하기
        int avgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

        // 누적 거리, 시간 구하기
        for (RunRecord record : runRecords) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
        }
        RunRecord RunRecords = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();

        // 갯수 구하기
        int statsCount = runRecords.size();

        return new AvgStatsDTO(RunRecords, statsCount, avgPace);
    }


}
