-- 사용자(User) 테이블에 더미 데이터 3개를 삽입합니다.

-- 1. 사용자 'ssar' (일반 사용자, 남)
INSERT INTO user_tb (username, email, profile_url, height, weight, gender, user_type, provider, user_tag,
                     flutter_token_id, created_at, run_level_id)
VALUES ('ssar', 'ssar@example.com', 'http://example.com/profiles/ssar.jpg', 175.0, 70.0, '남', '일반', 'kakao',
        '#A1B2C3', 'token_ssar_123', NOW(), 1);

-- 2. 사용자 'cos' (관리자, 여)
INSERT INTO user_tb (username, email, profile_url, height, weight, gender, user_type, provider, user_tag,
                     flutter_token_id, created_at, run_level_id)
VALUES ('cos', 'cos@example.com', 'http://example.com/profiles/cos.jpg', 168.0, 60.0, '여', '관리자', 'google',
        '#D4E5F6', 'token_cos_456', NOW(), 1);

-- 3. 사용자 'love' (일반 사용자, 여)
INSERT INTO user_tb (username, email, profile_url, height, weight, gender, user_type, provider, user_tag,
                     flutter_token_id, created_at, run_level_id)
VALUES ('love', 'love@example.com', 'http://example.com/profiles/love.jpg', 160.0, 55.0, '여', '일반', 'kakao',
        '#123ABC', 'token_love_789', NOW(), 1);

-- 4. 사용자 'haha' (일반 사용자, 남)
INSERT INTO user_tb (username, email, profile_url, height, weight, gender, user_type, provider, user_tag,
                     flutter_token_id, created_at, run_level_id)
VALUES ('haha', 'haha@example.com', 'http://example.com/profiles/haha.jpg', 180.0, 75.0, '남', '일반', 'kakao', '#456DEF',
        'token_haha_101', NOW(), 1);

-- 5. 사용자 'green' (일반 사용자, 여)
INSERT INTO user_tb (username, email, profile_url, height, weight, gender, user_type, provider, user_tag,
                     flutter_token_id, created_at, run_level_id)
VALUES ('green', 'green@example.com', 'http://example.com/profiles/green.jpg', 165.0, 58.0, '여', '일반', 'google',
        '#789GHI',
        'token_mia_202', NOW(), 1);

-- 6. 사용자 'leo' (일반 사용자, 남)
INSERT INTO user_tb (username, email, profile_url, height, weight, gender, user_type, provider, user_tag,
                     flutter_token_id, created_at, run_level_id)
VALUES ('leo', 'leo@example.com', 'http://example.com/profiles/leo.jpg', 172.0, 68.0, '남', '일반', 'kakao', '#321JKL',
        'token_leo_303', NOW(), 1);
