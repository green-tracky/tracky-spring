-- 사설 챌린지에서 사용할 리워드 종류를 미리 등록합니다.
-- (reward_type 컬럼에는 @Enumerated(EnumType.STRING)에 따라 Enum의 이름이 저장됩니다)
INSERT INTO reward_master_tb (reward_type, name, image_url, created_at)
VALUES ('GOLD', '금메달', 'https://example.com/rewards/gold.png', NOW()),
       ('SILVER', '은메달', 'https://example.com/rewards/silver.png', NOW()),
       ('BRONZE', '동메달', 'https://example.com/rewards/bronze.png', NOW()),
       ('PARTICIPATION', '참여상', 'https://example.com/rewards/participation.png', NOW());
