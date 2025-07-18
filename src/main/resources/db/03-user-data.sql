-- 사용자(User) 테이블에 더미 데이터 6개를 삽입합니다.
-- provider 값을 대문자로 수정했습니다.

-- 1. 사용자 'ssar' (GENERAL 사용자, MALE)
INSERT INTO user_tb (login_id, password, username, profile_url, height, weight, gender, location, letter,
                     user_type, provider, user_tag,
                     fcm_token, created_at, run_level_id)
VALUES ('KAKAO_123456789', 'password123', 'ssar', 'http://example.com/profiles/ssar.jpg', 175.0,
        70.0, 'MALE',
        '부산광역시', '안녕하세요, 러닝을 사랑하는 ssar입니다.', 'GENERAL', 'KAKAO',
        '#A1B2C3', 'token_ssar_123', '2025-05-30 12:00:00', 1);

-- 2. 사용자 'cos' (ADMIN, FEMALE)
INSERT INTO user_tb (login_id, password, username, profile_url, height, weight, gender, location, letter,
                     user_type, provider, user_tag,
                     fcm_token, created_at, run_level_id)
VALUES ('cos', 'password123', 'cos', 'http://example.com/profiles/cos.jpg', 168.0, 60.0, 'FEMALE',
        '서울특별시', '관리자 cos입니다. 잘 부탁드립니다.', 'ADMIN', 'GOOGLE',
        '#D4E5F6', 'token_cos_456', '2025-05-30 12:00:00', 1);

-- 3. 사용자 'love' (GENERAL 사용자, FEMALE)
INSERT INTO user_tb (login_id, password, username, profile_url, height, weight, gender, location, letter,
                     user_type, provider, user_tag,
                     fcm_token, created_at, run_level_id)
VALUES ('love', 'password123', 'love', 'http://example.com/profiles/love.jpg', 160.0, 55.0,
        'FEMALE', '부산광역시', '만나서 반갑습니다. 함께 달려요!', 'GENERAL', 'KAKAO',
        '#123ABC', 'token_love_789', '2025-05-30 12:00:00', 1);

-- 4. 사용자 'haha' (GENERAL 사용자, MALE)
INSERT INTO user_tb (login_id, password, username, profile_url, height, weight, gender, location, letter,
                     user_type, provider, user_tag,
                     fcm_token, created_at, run_level_id)
VALUES ('haha', 'password123', 'haha', 'http://example.com/profiles/haha.jpg', 180.0, 75.0, 'MALE',
        '인천광역시', '같이 뛸 준비 되셨나요? haha입니다.', 'GENERAL', 'KAKAO',
        '#456DEF', 'token_haha_101', '2025-05-30 12:00:00', 1);

-- 5. 사용자 'green' (GENERAL 사용자, FEMALE)
INSERT INTO user_tb (login_id, password, username, profile_url, height, weight, gender, location, letter,
                     user_type, provider, user_tag,
                     fcm_token, created_at, run_level_id)
VALUES ('green', 'password123', 'green', 'http://example.com/profiles/green.jpg', 165.0, 58.0,
        'FEMALE', '대구광역시', '초보 러너 green입니다. 함께 성장해요!', 'GENERAL', 'GOOGLE',
        '#789GHI', 'token_mia_202', '2025-05-30 12:00:00', 1);

-- 6. 사용자 'leo' (GENERAL 사용자, MALE)
INSERT INTO user_tb (login_id, password, username, profile_url, height, weight, gender, location, letter,
                     user_type, provider, user_tag,
                     fcm_token, created_at, run_level_id)
VALUES ('leo', 'password123', 'leo', 'http://example.com/profiles/leo.jpg', 172.0, 68.0, 'MALE',
        '부산광역시', 'leo입니다. 꾸준함이 답이라고 생각합니다.', 'GENERAL', 'KAKAO',
        '#321JKL', 'token_leo_303', '2025-05-30 12:00:00', 1);
