-- 친구 요청 (보낸 사람: 1, 받은 사람: 2), 아직 응답 없음
INSERT INTO friend_invite_tb (from_user_id, to_user_id, created_at, status, responsed_at)
VALUES (1, 2, NOW(), 'WAITING', NULL),
       (6, 1, NOW(), 'WAITING', NULL),
       (4, 1, NOW(), 'WAITING', NULL),
-- 친구 요청 (보낸 사람: 3, 받은 사람: 1), 수락됨
       (3, 1, NOW(), 'ACCEPTED', NOW() - INTERVAL '6' DAY),
-- 친구 요청 (보낸 사람: 2, 받은 사람: 4), 거절됨
       (2, 4, NOW(), 'REJECTED', NOW() - INTERVAL '1' DAY);
