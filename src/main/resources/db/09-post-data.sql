-- 1. 게시글 작성 (ssar가 작성, runRecord 연결)
INSERT INTO post_tb (user_id, run_record_id, content, created_at, updated_at)
VALUES (1, 1, 'ssar의 러닝 기록을 공유합니다.', NOW(), NOW()),
       (3, null, 'love의 러닝 기록을 공유합니다.', NOW(), NOW());