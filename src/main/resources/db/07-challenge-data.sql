-- 추가된 필드: challenge_year, challenge_month, week_of_month, period_type
INSERT INTO challenge_tb (name, sub, description, start_date, end_date, target_distance, is_in_progress, creator_id,
                          created_at, type, img_index, challenge_year, challenge_month, week_of_month, period_type)
VALUES ('6월 5k 챌린지', '이번 주 5km를 달려보세요.', '주간 챌린지를 통해 나의 한계를 뛰어넘어 보세요. 이번 주 5km를 달리면 특별한 완주자 기록을 달성할 수 있습니다.',
        '2025-06-01 00:00:00', '2025-06-30 23:59:59', 5000, true, 2, '2025-06-01 00:00:00', 'PUBLIC',
        null, 2025, 6, null, 'MONTHLY');

-- 2. 10km 공식 챌린지
INSERT INTO challenge_tb (name, sub, description, start_date, end_date, target_distance, is_in_progress, creator_id,
                          created_at, type, img_index, challenge_year, challenge_month, week_of_month, period_type)
VALUES ('6월 15k 챌린지', '6월 한 달 동안 15km를 달성해보세요!', '꾸준함이 실력! 6월 한 달 동안 10km를 달성하고 성취감을 느껴보세요.',
        '2025-06-01 00:00:00', '2025-06-30 23:59:59', 10000, true, 2, '2025-06-01 00:00:01', 'PUBLIC',
        null, 2025, 6, null, 'MONTHLY');

-- 3. 15km 공식 챌린지
INSERT INTO challenge_tb (name, sub, description, start_date, end_date, target_distance, is_in_progress, creator_id,
                          created_at, type, img_index, challenge_year, challenge_month, week_of_month, period_type)
VALUES ('6월 25k 챌린지', '6월 한 달 동안 25km를 달성해보세요!', '이제 당신도 러너! 15km의 거리를 정복하고 특별한 보상을 받으세요.',
        '2025-06-01 00:00:00', '2025-06-30 23:59:59', 15000, true, 2, '2025-06-01 00:00:02', 'PUBLIC',
        null, 2025, 6, null, 'MONTHLY');

-- 4. 50km 공식 챌린지
INSERT INTO challenge_tb (name, sub, description, start_date, end_date, target_distance, is_in_progress, creator_id,
                          created_at, type, img_index, challenge_year, challenge_month, week_of_month, period_type)
VALUES ('6월 50k 챌린지', '6월 한 달 동안 50km를 달성해보세요!', '인내와 열정의 상징, 50km 완주에 도전하고 당신의 한계를 증명하세요.',
        '2025-06-01 00:00:00', '2025-06-30 23:59:59', 50000, true, 2, '2025-06-01 00:00:03', 'PUBLIC',
        null, 2025, 6, null, 'MONTHLY');

-- 5. 100km 공식 챌린지
INSERT INTO challenge_tb (name, sub, description, start_date, end_date, target_distance, is_in_progress, creator_id,
                          created_at, type, img_index, challenge_year, challenge_month, week_of_month, period_type)
VALUES ('6월 100k 챌린지', '6월 한 달 동안 100km를 달성해보세요!', '상위 1%를 위한 궁극의 도전! 100km를 완주하고 명예의 전당에 오르세요.',
        '2025-06-01 00:00:00', '2025-06-30 23:59:59', 100000, true, 2, '2025-06-01 00:00:04', 'PUBLIC',
        null, 2025, 6, null, 'MONTHLY');

-- 6. 1km 사설 챌린지
INSERT INTO challenge_tb (name, sub, description, start_date, end_date, target_distance, is_in_progress, creator_id,
                          created_at, type, img_index, challenge_year, challenge_month, week_of_month, period_type)
VALUES ('가볍게 1km 달리기', '', '', '2025-06-09 00:00:00', '2025-06-15 23:59:59', 1000, true, 1, '2025-06-09 00:00:00',
        'PRIVATE',
        1, null, null, null, 'ETC');

