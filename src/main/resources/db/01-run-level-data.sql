INSERT INTO run_level_tb (name, min_distance, max_distance, description, image_url, sort_order)
VALUES ('옐로우', 0, 49999, '0~49.99킬로미터', 'https://example.com/images/yellow.png', 1),
       ('오렌지', 50000, 249999, '50.00~249.9킬로미터', 'https://example.com/images/orange.png', 2),
       ('그린', 250000, 999999, '250.0~999.9킬로미터', 'https://example.com/images/green.png', 3),
       ('블루', 1000000, 2499000, '1,000~2,499킬로미터', 'https://example.com/images/blue.png', 4);