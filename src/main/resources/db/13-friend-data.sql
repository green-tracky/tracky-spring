-- user_id 1번 유저가 user_id 2, 3, 4와 친구 관계인 더미 데이터
INSERT INTO friend_tb (from_user_id, to_user_id, created_at)
VALUES (1, 3, '2025-01-10 09:00:00'),
       (1, 4, '2025-03-12 14:30:00'),
       (1, 5, '2025-06-25 17:45:00'),
       (1, 6, '2025-06-25 17:45:00');
