INSERT INTO challenge_tb
(name, sub, description, start_date, end_date, target_distance, is_in_progress, reward_title, reward_image_url,
 creator_id, created_at)
VALUES ('6월 주간 챌린지', '이번 주 5km를 달려보세요.', '주간 챌린지를 통해 나의 한계를 뛰어넘어 보세요. 이번 주 5km를 달리면 특별한 완주자 기록을 달성할 수 있습니다.',
        '2025-06-01 00:00:00', '2025-06-30 23:59:59', 5000, true,
        '5km 완주 뱃지', 'https://example.com/rewards/5km_badge.png', 1, '2025-06-01 00:00:00'),
       ('6월 주간 챌린지', '10km 도전!', '6월 한 달 동안 10km를 달성해보세요!', '2025-06-01 00:00:00', '2025-06-30 23:59:59', 10000, true,
        '10km 완주 뱃지', 'https://example.com/rewards/10km_badge.png', 1, '2025-06-01 00:00:00'),
       ('6월 15km 챌린지', '15km 도전!', '6월 한 달 동안 15km를 달성해보세요!', '2025-06-01 00:00:00', '2025-06-30 23:59:59', 15000, true,
        '15km 완주 뱃지', 'https://example.com/rewards/15km_badge.png', 1, '2025-06-01 00:00:00'),
       ('6월 50km 챌린지', '50km 도전!', '6월 한 달 동안 50km를 달성해보세요!', '2025-06-01 00:00:00', '2025-06-30 23:59:59', 50000, true,
        '50km 완주 뱃지', 'https://example.com/rewards/50km_badge.png', 1, '2025-06-01 00:00:00'),
       ('6월 100km 챌린지', '100km 도전!', '6월 한 달 동안 100km를 달성해보세요!', '2025-06-01 00:00:00', '2025-06-30 23:59:59', 100000,
        true, '100km 완주 뱃지', 'https://example.com/rewards/100km_badge.png', 1, '2025-06-01 00:00:00');