-- 1. 게시글 작성 (ssar가 작성, runRecord 연결)
INSERT INTO post_tb (user_id, run_record_id, title, content, created_at, updated_at)
VALUES (1, 1, '첫 게시글입니다.', 'ssar의 러닝 기록을 공유합니다.', NOW(), NOW()),
       (3, null, '두번째 게시글입니다.', 'love의 러닝 기록을 공유합니다.', NOW(), NOW());