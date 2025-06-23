
-- RunRecord 더미데이터 (예: id=1,2,3)
INSERT INTO run_record_tb (id, title, total_distance_meters, total_duration_seconds, totalcalories, avg_pace, best_pace, memo, intensity, place, created_at)
VALUES
(1, 'Short Run', 300, 180, 25, 10.0, 9.5, '짧은 러닝', 5, '도로', CURRENT_TIMESTAMP),
(2, 'Medium Run', 500, 300, 45, 10.0, 9.0, '중간 거리 러닝', 6, '트랙', CURRENT_TIMESTAMP),
(3, 'Long Run', 1000, 660, 80, 11.0, 10.5, '긴 러닝', 7, '산길', CURRENT_TIMESTAMP);

-- RunSegment 더미데이터 (id, distanceMeters, durationSeconds, startDate, endDate, pace, calories, runRecord_id)
INSERT INTO run_segment_tb (id, distance_meters, duration_seconds, start_date, end_date, pace, calories, run_record_id) VALUES
(1, 100, 60, '2025-06-23 06:00:00', '2025-06-23 06:01:00', 60.0, 10, 1),
(2, 200, 120, '2025-06-23 06:01:00', '2025-06-23 06:03:00', 60.0, 20, 1),
(3, 300, 180, '2025-06-23 06:03:00', '2025-06-23 06:06:00', 60.0, 30, 2);


-- RunCoordinate 더미데이터 (run_record_id = 1, 2초마다 증가, 37.0001씩 위도 증가, 127.0001씩 경도 증가)
INSERT INTO run_coordinate_tb (lat, lon, created_at, run_segment_id) VALUES
(37.0, 127.0, '2025-06-23 06:00:00', 1),
(37.0001, 127.0001, '2025-06-23 06:00:02', 1),
(37.0002, 127.0002, '2025-06-23 06:00:04', 1),
(37.0003, 127.0003, '2025-06-23 06:00:06', 1),
(37.0004, 127.0004, '2025-06-23 06:00:08', 1),
(37.0005, 127.0005, '2025-06-23 06:00:10', 1),
(37.0006, 127.0006, '2025-06-23 06:00:12', 1),
(37.0007, 127.0007, '2025-06-23 06:00:14', 1),
(37.0008, 127.0008, '2025-06-23 06:00:16', 1);  


