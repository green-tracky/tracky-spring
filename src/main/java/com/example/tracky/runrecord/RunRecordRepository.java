package com.example.tracky.runrecord;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RunRecordRepository {

    private final EntityManager em;

    /**
     * 테스트용 findById
     * 삭제용 findById
     *
     * @param id -> runRecordId
     * @return
     */
    public Optional<RunRecord> findById(Integer id) {
        Query query = em.createQuery("select r from RunRecord r where r.id = :id", RunRecord.class);
        query.setParameter("id", id);

        try {
            return Optional.of((RunRecord) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

    /**
     * RunRecord 엔티티 퍼시스트 컨텍스트에 저장
     *
     * @param runRecord
     * @return RunRecord
     */
    public RunRecord save(RunRecord runRecord) {
        em.persist(runRecord);
        return runRecord;
    }

    /**
     * join fetch
     * <p>
     * - runSegments
     * <p>
     * 좌표를 가져오지 않는 이유: 좌표가 너무 많으면 카테시안 곱 문제가 발생하여 너무 많은 row를 만들게 된다. 따라서 구간까지만 조회하고 배치로 좌표값을 가져오자
     * <p>
     * distinct 를 사용하는 이유: 메모리에서 러닝 객체를 더 만들지 마라
     *
     * @param id -> runRecordId
     * @return
     */
    public Optional<RunRecord> findByIdJoin(Integer id) {
        Query query = em.createQuery("select distinct r from RunRecord r join fetch r.runSegments where r.id = :id", RunRecord.class);
        query.setParameter("id", id);

        try {
            return Optional.of((RunRecord) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

    /**
     * 러닝 기록 삭제
     *
     * @param runRecord
     */
    public void delete(RunRecord runRecord) {
        em.remove(runRecord);
    }

    /**
     * 사용자의 총 누적 러닝 거리를 조회하는 메서드
     *
     * @param userId
     * @return
     */
    public Integer findTotalDistanceByUserId(Integer userId) {
        Query query = em.createQuery("select sum(r.totalDistanceMeters) from RunRecord r where r.user.id = :userId", Long.class);
        query.setParameter("userId", userId);
        Long totalDistance = (Long) query.getSingleResult();
        return totalDistance == null ? 0 : totalDistance.intValue();
    }

    /**
     * <pre>
     * 사용자의 특정 월의 러닝 횟수를 조회하는 메서드
     * '첫 시작' 뱃지 조건(매달 첫 러닝) 검사에 사용
     * </pre>
     *
     * @param userId
     * @param yearMonth YearMonth.now() 값을 넣으면 됨
     * @return
     */
    public Integer countByUserIdAndYearMonth(Integer userId, YearMonth yearMonth) {
        // JPQL의 FUNCTION 키워드를 사용하여 데이터베이스의 네이티브 날짜 함수(YEAR, MONTH)를 호출
        Query query = em.createQuery("select count(r) from RunRecord r where r.user.id = :userId and function('YEAR', r.createdAt) = :year and function('MONTH', r.createdAt) = :month", Long.class);
        query.setParameter("userId", userId);
        query.setParameter("year", yearMonth.getYear());
        query.setParameter("month", yearMonth.getMonth());
        Long totalCount = (Long) query.getSingleResult();
        return totalCount == null ? 0 : totalCount.intValue();
    }

    /**
     * 특정 사용자(userId)가 특정 연월(yearMonth)에 달린 총 거리를 미터(m) 단위로 조회합니다.
     *
     * @param userId    조회할 사용자의 ID
     * @param yearMonth 조회할 연월 (예: YearMonth.of(2025, 6))
     * @return 해당 사용자의 해당 월 총 달리기 거리 (미터 단위). 기록이 없으면 0을 반환합니다.
     */
    public Integer findTotalDistanceByUserIdAndYearMonth(Integer userId, YearMonth yearMonth) {
        Query query = em.createQuery("select sum (r.totalDistanceMeters) from RunRecord r where r.user.id = :userId and function('YEAR', r.createdAt) = :year and function('MONTH', r.createdAt) = :month", Long.class);
        query.setParameter("userId", userId);
        query.setParameter("year", yearMonth.getYear());
        query.setParameter("month", yearMonth.getMonthValue());
        Long totalDistance = (Long) query.getSingleResult();
        return totalDistance == null ? 0 : totalDistance.intValue();
    }

    /**
     * RunRecord 엔티티 전체 조회
     *
     * @return
     */
    public List<RunRecord> findAllByUserId(Integer userId) {
        Query query = em.createQuery("select r from RunRecord r where r.user.id = : userId", RunRecord.class);
        query.setParameter("userId", userId);
        List<RunRecord> runRecords = query.getResultList();
        return runRecords;
    }

    /**
     * 최근 기록 3개 불러오기
     *
     * @return
     */
    public List<RunRecord> findTop3ByUserIdOrderByCreatedAtJoinBadgeAchv(Integer userId) {
        Query query = em.createQuery("select r from RunRecord r left outer join fetch r.runBadgeAchvs rba where r.user.id = : userId order by r.createdAt desc", RunRecord.class);
        query.setParameter("userId", userId);
        query.setMaxResults(3);
        List<RunRecord> recentRuns = query.getResultList();
        return recentRuns;
    }

    /**
     * 특정 기간 동안 생성된 RunRecord 엔티티를 조회
     * <p></p>
     * - createdAt 기준으로 시작일~종료일 사이의 기록만 필터링
     *
     * @param start 시작일시
     * @param end   종료일시
     * @return 기간 내 러닝 기록 리스트
     */
    public List<RunRecord> findAllByCreatedAtBetween(Integer userId, LocalDateTime start, LocalDateTime end) {
        Query query = em.createQuery(
                "SELECT r FROM RunRecord r WHERE r.user.id = :userId AND r.createdAt BETWEEN :start AND :end",
                RunRecord.class);
        query.setParameter("userId", userId);
        query.setParameter("start", start);
        query.setParameter("end", end);
        List<RunRecord> runRecords = query.getResultList();
        return runRecords;
    }

}