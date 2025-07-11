-- 친구 요청 (보낸 사람: 1, 받은 사람: 2), 아직 응답 없음
INSERT INTO user_challenge_tb (status, from_user_id, to_user_id, challenge_id, created_at, response_at)
VALUES ('PENDING', 2, 1, 1, NOW(), NULL),
       ('PENDING', 3, 1, 2, NOW(), NULL);

