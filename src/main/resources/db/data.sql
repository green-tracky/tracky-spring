
-- RunRecord 더미데이터 (예: id=1,2,3)
INSERT INTO run_record_tb ( id, title, total_distance_meters, total_duration_seconds, calories, memo, intensity, place, created_at)
VALUES (1, '테스트 러닝', 500, 240, 50, '예제 러닝 기록입니다.', 5, '도로', CURRENT_TIMESTAMP);

-- RunSegment 더미데이터 (id, distanceMeters, durationSeconds, startDate, endDate, pace, calories, runRecord_id)
INSERT INTO run_segment_tb (id, distance_meters, duration_seconds, start_date, end_date, run_record_id)
VALUES
(1, 300, 120, '2025-06-23 06:00:00', '2025-06-23 06:02:00', 1),
(2, 200, 120, '2025-06-23 06:02:00', '2025-06-23 06:04:00', 1);

-- RunCoordinate 더미데이터 (run_record_id = 1, 2초마다 증가, 37.0001씩 위도 증가, 127.0001씩 경도 증가)
INSERT INTO run_coordinate_tb (
    lat, lon, created_at, run_segment_id
) VALUES
-- 구간 1
(37.0, 127.0, '2025-06-23 06:00:00', 1),
(37.0001, 127.0001, '2025-06-23 06:00:02', 1),
(37.0, 127.0, '2025-06-23 06:00:00', 2),
(37.0001, 127.0001, '2025-06-23 06:00:02', 2);


