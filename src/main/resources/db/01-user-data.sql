-- 사용자(User) 테이블에 더미 데이터 3개를 삽입합니다.

-- 1. 사용자 'ssar' (일반 사용자, 남)
INSERT INTO user_tb (username, email, profile_url, height, weight, gender, user_type, provider, user_tag,
                     flutter_token_id, created_at)
VALUES ('ssar', 'ssar@example.com', 'http://example.com/profiles/ssar.jpg', 175.0, 70.0, '남', '일반', 'kakao',
        '#A1B2C3', 'token_ssar_123', NOW());

-- 2. 사용자 'cos' (관리자, 여)
INSERT INTO user_tb (username, email, profile_url, height, weight, gender, user_type, provider, user_tag,
                     flutter_token_id, created_at)
VALUES ('cos', 'cos@example.com', 'http://example.com/profiles/cos.jpg', 168.0, 60.0, '여', '관리자', 'google',
        '#D4E5F6', 'token_cos_456', NOW());

-- 3. 사용자 'love' (일반 사용자, 여)
INSERT INTO user_tb (username, email, profile_url, height, weight, gender, user_type, provider, user_tag,
                     flutter_token_id, created_at)
VALUES ('love', 'love@example.com', 'http://example.com/profiles/love.jpg', 160.0, 55.0, '여', '일반', 'kakao',
        '#123ABC', 'token_love_789', NOW());

INSERT INTO run_level_tb (name, min_distance, max_distance, description, image_url, sort_order)
VALUES ('옐로우', 0, 49999, '입문 러너', 'https://example.com/images/yellow.png', 1),
       ('오렌지', 50000, 249999, '기초 러너', 'https://example.com/images/orange.png', 2),
       ('그린', 250000, 999999, '중급 러너', 'https://example.com/images/green.png', 3),
       ('블루', 1000000, 2499000, '상급 러너', 'https://example.com/images/blue.png', 4);
