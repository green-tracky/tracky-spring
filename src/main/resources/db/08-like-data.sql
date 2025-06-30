-- 1. love(3번)가 1번글에 좋아요
-- 2. 게시글 작성자(1번)가 댓글 1번에 좋아요
INSERT INTO like_tb (post_id, user_id, comment_id, created_at)
VALUES (1, 3, NULL, NOW()),
       (NULL, 1, 1, NOW());