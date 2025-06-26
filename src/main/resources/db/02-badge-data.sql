-- badge_tb 테이블에 데이터 INSERT (type을 한글로 변경)
-- 주의: 이 스크립트를 실행하기 전에 badge_tb 테이블에 type VARCHAR(255) 컬럼이 있어야 합니다.
INSERT INTO badge_tb (id, name, description, imageUrl, type)
VALUES (1, '첫 시작', '매달 첫 러닝을 완료했어요!', 'https://example.com/badges/first_run.png', '업적'),
       (2, '1K 최고 기록', '1km를 가장 빠른 시간 내에 완주했어요!', 'https://example.com/badges/1k_best.png', '기록'),
       (3, '5K 최고 기록', '5km를 가장 빠른 시간 내에 완주했어요!', 'https://example.com/badges/5k_best.png', '기록'),
       (4, '브론즈', '총 러닝 거리 10km를 달성했어요!', 'https://example.com/badges/bronze.png', '업적'),
       (5, '실버', '총 러닝 거리 20km를 달성했어요!', 'https://example.com/badges/silver.png', '업적'),
       (6, '골드', '총 러닝 거리 40km를 달성했어요!', 'https://example.com/badges/gold.png', '업적'),
       (7, '플래티넘', '총 러닝 거리 80km를 달성했어요!', 'https://example.com/badges/platinum.png', '업적');
