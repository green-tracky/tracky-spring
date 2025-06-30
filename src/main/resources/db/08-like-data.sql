-- 1. love가 1번글에 좋아요
-- 2. 게시글 작성자가 (1번) 댓글 1번을 좋아요
INSERT INTO like_tb (post_id, user_id, comment_id, created_at)
VALUES (1, 3, NULL, NOW()),
       (1, 1, 1, NOW());