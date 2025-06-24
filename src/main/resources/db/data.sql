
-- RunRecord 더미데이터 (예: id=1,2,3)
INSERT INTO run_record_tb ( id, title, total_distance_meters, total_duration_seconds, calories, memo, intensity, place, created_at)
VALUES (1, '테스트 러닝', 500, 240, 50, '예제 러닝 기록입니다.', 5, '도로', CURRENT_TIMESTAMP);
INSERT INTO run_record_tb (id, title, total_distance_meters, total_duration_seconds, calories, memo, intensity, place, created_at)
VALUES (2, '두번째 러닝', 1000, 400, 100, '두번째 예제 러닝 기록입니다.', 4, '공원', CURRENT_TIMESTAMP);

-- RunSegment 더미데이터 (id, distanceMeters, durationSeconds, startDate, endDate, pace, calories, runRecord_id)
INSERT INTO run_segment_tb (id, distance_meters, duration_seconds, start_date, end_date, run_record_id)
VALUES
(1, 300, 120, '2025-06-23 06:00:00', '2025-06-23 06:02:00', 1),
(2, 200, 120, '2025-06-23 06:02:00', '2025-06-23 06:04:00', 1),
(3, 600, 300, '2025-06-24 07:00:00', '2025-06-24 07:05:00', 2),
(4, 400, 180, '2025-06-24 07:05:00', '2025-06-24 07:08:00', 2);

-- RunCoordinate 더미데이터 (run_record_id = 1, 2초마다 증가, 37.0001씩 위도 증가, 127.0001씩 경도 증가)
INSERT INTO run_coordinate_tb (lat, lon, created_at, run_segment_id)
VALUES
-- 구간 1
(37.0, 127.0, '2025-06-23 06:00:00', 1),
(37.0001, 127.0001, '2025-06-23 06:00:02', 1),
-- 구간 2
(37.0, 127.0, '2025-06-23 06:00:00', 2),
(37.0001, 127.0001, '2025-06-23 06:00:02', 2),
-- 구간 3
(37.0010, 127.0010, '2025-06-24 07:00:00', 3),
(37.0011, 127.0011, '2025-06-24 07:00:02', 3),
(37.0012, 127.0012, '2025-06-24 07:00:04', 3),
-- 구간 4
(37.0013, 127.0013, '2025-06-24 07:05:00', 4),
(37.0014, 127.0014, '2025-06-24 07:05:02', 4);


