INSERT INTO run_record_tb (id, title, memo, calories, total_distance_meters, total_duration_seconds, intensity, place, created_at)
VALUES (1, '부산 해운대 아침 달리기', '날씨가 좋아서 상쾌했다. 다음엔 더 멀리 가봐야지.', 200, 2000, 784, 0, NULL, '2025-06-24 05:58:12.366');

INSERT INTO run_segment_tb (id, distance_meters, duration_seconds, start_date, end_date, run_record_id)
VALUES 
(1, 1000, 430, '2025-06-21 21:30:00', '2025-06-21 21:37:10', 1),
(2, 1000, 354, '2025-06-21 21:37:11', '2025-06-21 21:43:05', 1);

INSERT INTO run_coordinate_tb (id, lat, lon, created_at, run_segment_id)
VALUES
(1, 35.1587, 129.1604, '2025-06-21 21:30:00', 1),
(2, 35.1595, 129.1612, '2025-06-21 21:33:45', 1),
(3, 35.1602, 129.162,  '2025-06-21 21:37:10', 1),
(4, 35.161,  129.1628, '2025-06-21 21:40:00', 2),
(5, 35.1618, 129.1635, '2025-06-21 21:43:05', 2);
