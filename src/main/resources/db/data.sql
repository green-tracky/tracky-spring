INSERT INTO run_badge_tb (name, description, image_url) VALUES
('첫 시작', '매달 첫 러닝을 완료했어요!', 'https://example.com/badges/first_run.png'),
('1K 최고 기록', '1km를 가장 빠른 시간 내에 완주했어요!', 'https://example.com/badges/1k_best.png'),
('5K 최고 기록', '5km를 가장 빠른 시간 내에 완주했어요!', 'https://example.com/badges/5k_best.png'),
('브론즈', '총 러닝 거리 10km를 달성했어요!', 'https://example.com/badges/bronze.png'),
('실버', '총 러닝 거리 20km를 달성했어요!', 'https://example.com/badges/silver.png'),
('골드', '총 러닝 거리 40km를 달성했어요!', 'https://example.com/badges/gold.png'),
('플래티넘', '총 러닝 거리 80km를 달성했어요!', 'https://example.com/badges/platinum.png');

INSERT INTO challenge_tb (name, sub, description, start_date, end_date, target_distance, status, created_at) VALUES
('6월 5km 챌린지', '5km 도전!', '6월 한 달 동안 5km를 달성해보세요!', '2025-06-01', '2025-06-30', 5.0, '진행중', '2025-06-01 00:00:00'),
('6월 10km 챌린지', '10km 도전!', '6월 한 달 동안 10km를 달성해보세요!', '2025-06-01', '2025-06-30', 10.0, '진행중', '2025-06-01 00:00:00'),
('6월 15km 챌린지', '15km 도전!', '6월 한 달 동안 15km를 달성해보세요!', '2025-06-01', '2025-06-30', 15.0, '진행중', '2025-06-01 00:00:00'),
('6월 50km 챌린지', '50km 도전!', '6월 한 달 동안 50km를 달성해보세요!', '2025-06-01', '2025-06-30', 50.0, '진행중', '2025-06-01 00:00:00'),
('6월 100km 챌린지', '100km 도전!', '6월 한 달 동안 100km를 달성해보세요!', '2025-06-01', '2025-06-30', 100.0, '진행중', '2025-06-01 00:00:00');
