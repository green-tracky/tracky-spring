-- 1. love(3번)가 1번글에 좋아요
-- 2. 게시글 작성자(1번)가 댓글 1번에 좋아요
INSERT INTO like_tb (user_id, post_id, comment_id, created_at)
VALUES (3, 1, NULL, NOW()),
       (1, NULL, 1, NOW()),
       (1, 2, NULL, NOW());
