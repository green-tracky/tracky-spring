-- 친구 요청 (보낸 사람: 1, 받은 사람: 2), 아직 응답 없음
INSERT INTO friend_invite_tb (from_user_id, to_user_id, created_at, status, responded_at)
VALUES (1, 2, '2025-06-24', 'PENDING', NULL),
       (6, 1, '2025-06-12', 'PENDING', NULL),
       (4, 1, '2025-06-11', 'PENDING', NULL),
-- 친구 요청 (보낸 사람: 3, 받은 사람: 1), 수락됨
       (3, 1, '2025-06-27', 'ACCEPTED', '2025-06-29 00:00:00'),
-- 친구 요청 (보낸 사람: 2, 받은 사람: 4), 거절됨
       (2, 4, '2025-06-04', 'REJECTED', '2025-06-05 00:00:00');
