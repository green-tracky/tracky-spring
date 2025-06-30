-- 1. 5km 공식 챌린지
-- 부모 테이블: 공통 정보 저장
INSERT INTO challenge_tb (id, name, sub, description, start_date, end_date, target_distance, is_in_progress, creator_id,
                          created_at, challenge_type)
VALUES (1, '6월 주간 챌린지', '이번 주 5km를 달려보세요.', '주간 챌린지를 통해 나의 한계를 뛰어넘어 보세요. 이번 주 5km를 달리면 특별한 완주자 기록을 달성할 수 있습니다.',
        '2025-06-01 00:00:00', '2025-06-30 23:59:59', 5000, true, 1, NOW(), 'PUBLIC');
-- 자식 테이블: PublicChallenge만의 고유 정보 저장
INSERT INTO public_challenge_tb (id, reward_title, reward_image_url)
VALUES (1, '5km 완주 뱃지', 'https://example.com/rewards/5km_badge.png');


-- 2. 10km 공식 챌린지
-- 부모 테이블
INSERT INTO challenge_tb (id, name, sub, description, start_date, end_date, target_distance, is_in_progress, creator_id,
                          created_at, challenge_type)
VALUES (2, '6월 10km 도전!', '6월 한 달 동안 10km를 달성해보세요!', '꾸준함이 실력! 6월 한 달 동안 10km를 달성하고 성취감을 느껴보세요.', '2025-06-01 00:00:00',
        '2025-06-30 23:59:59', 10000, true, 1, NOW(), 'PUBLIC');
-- 자식 테이블
INSERT INTO public_challenge_tb (id, reward_title, reward_image_url)
VALUES (2, '10km 완주 뱃지', 'https://example.com/rewards/10km_badge.png');


-- 3. 15km 공식 챌린지
-- 부모 테이블
INSERT INTO challenge_tb (id, name, sub, description, start_date, end_date, target_distance, is_in_progress, creator_id,
                          created_at, challenge_type)
VALUES (3, '6월 15km 챌린지', '15km 도전!', '6월 한 달 동안 15km를 달성해보세요!', '이제 당신도 러너! 15km의 거리를 정복하고 특별한 보상을 받으세요.',
        '2025-06-01 00:00:00', '2025-06-30 23:59:59', 15000, true, 1, NOW(), 'PUBLIC');
-- 자식 테이블
INSERT INTO public_challenge_tb (id, reward_title, reward_image_url)
VALUES (3, '15km 완주 뱃지', 'https://example.com/rewards/15km_badge.png');


-- 4. 50km 공식 챌린지
-- 부모 테이블
INSERT INTO challenge_tb (id, name, sub, description, start_date, end_date, target_distance, is_in_progress, creator_id,
                          created_at, challenge_type)
VALUES (4, '6월 50km 챌린지', '50km 도전!', '6월 한 달 동안 50km를 달성해보세요!', '인내와 열정의 상징, 50km 완주에 도전하고 당신의 한계를 증명하세요.',
        '2025-06-01 00:00:00', '2025-06-30 23:59:59', 50000, true, 1, NOW(), 'PUBLIC');
-- 자식 테이블
INSERT INTO public_challenge_tb (id, reward_title, reward_image_url)
VALUES (4, '50km 완주 뱃지', 'https://example.com/rewards/50km_badge.png');


-- 5. 100km 공식 챌린지
-- 부모 테이블
INSERT INTO challenge_tb (id, name, sub, description, start_date, end_date, target_distance, is_in_progress, creator_id,
                          created_at, challenge_type)
VALUES (5, '6월 100km 챌린지', '100km 도전!', '6월 한 달 동안 100km를 달성해보세요!', '상위 1%를 위한 궁극의 도전! 100km를 완주하고 명예의 전당에 오르세요.',
        '2025-06-01 00:00:00', '2025-06-30 23:59:59', 100000, true, 1, NOW(), 'PUBLIC');
-- 자식 테이블
INSERT INTO public_challenge_tb (id, reward_title, reward_image_url)
VALUES (5, '100km 완주 뱃지', 'https://example.com/rewards/100km_badge.png');

-- 6. 1km 사설 챌린지
-- 부모 테이블
INSERT INTO challenge_tb (id, name, sub, description, start_date, end_date, target_distance, is_in_progress, creator_id,
                          created_at, challenge_type)
VALUES (6, '가볍게 1km 달리기', '오늘의 작은 성취!', '부담없이 1km만 달려봐요! 완주만 해도 참여상과 동메달을 드려요.', '2025-06-15 00:00:00',
        '2025-07-15 23:59:59', 1000, true, 1, NOW(), 'PRIVATE');
-- 자식 테이블
INSERT INTO private_challenge_tb (id)
VALUES (6);

-- 연결 테이블: 6번 챌린지에 '동메달'(id=3)과 '참여상'(id=4) 보상을 연결합니다.
INSERT INTO private_challenge_reward_tb (private_challenge_id, reward_master_id)
VALUES (6, 3), -- 6번 챌린지에 동메달 연결
       (6, 4); -- 6번 챌린지에 참여상 연결
