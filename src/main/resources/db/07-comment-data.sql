-- 1. love가 댓글 작성
INSERT INTO comment_tb (post_id, user_id, content, created_at, updated_at)
VALUES (1, 3, '멋진 기록이에요!', NOW(), NOW());
-- 2. ssar가 대댓글 작성 (parent_id = 1)
INSERT INTO comment_tb (post_id, user_id, content, parent_id, created_at, updated_at)
VALUES (1, 1, '감사합니다 :)', 1, NOW(), NOW());