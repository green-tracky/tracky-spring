-- 친구 요청 (보낸 사람: 1, 받은 사람: 2), 아직 응답 없음
INSERT INTO challenge_invite_tb (status, from_user_id, to_user_id, challenge_id, created_at, response_at)
VALUES ('PENDING', 6, 1, 1, '2025-07-11 15:36:00', NULL),
       ('PENDING', 3, 1, 2, '2025-07-05 15:36:00', NULL);

