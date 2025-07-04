INSERT INTO user_challenge_reward_tb (user_id, challenge_id, reward_master_id, reward_name, reward_image_url,
                                      received_at)
VALUES
-- 금메달 1개
(1, 1, 1, '금메달', 'https://example.com/rewards/gold.png', '2025-01-15 10:00:00'),
-- 은메달 2개
(1, 2, 2, '은메달', 'https://example.com/rewards/silver.png', '2025-05-18 11:20:00'),
(1, 3, 2, '은메달', 'https://example.com/rewards/silver.png', '2025-06-07 08:45:00'),
-- 동메달 3개
(1, 4, 3, '동메달', 'https://example.com/rewards/bronze.png', '2025-06-02 08:55:00'),
(1, 5, 3, '동메달', 'https://example.com/rewards/bronze.png', '2025-06-28 11:35:00'),
(1, 6, 3, '동메달', 'https://example.com/rewards/bronze.png', '2025-07-03 16:45:00');

