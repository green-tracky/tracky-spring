-- user_id 1번 유저가 user_id 2, 3, 4와 친구 관계인 더미 데이터
INSERT INTO friend_tb (from_user_id, to_user_id, created_at)
VALUES (1, 2, '2025-01-10 09:00:00'),
       (1, 3, '2025-03-12 14:30:00'),
       (1, 4, '2025-06-25 17:45:00');

-- -- 쌍방 관계일 경우: to_user → from_user도 저장
-- INSERT INTO friend_tb (from_user_id, to_user_id, created_at)
-- VALUES
--     (2, 1, '2025-01-10 09:00:00'),
--     (3, 1, '2025-03-12 14:30:00'),
--     (4, 1, '2025-06-25 17:45:00');
