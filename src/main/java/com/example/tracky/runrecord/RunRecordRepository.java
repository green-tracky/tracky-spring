package com.example.tracky.runrecord;

import com.example.tracky._core.constants.Constants;
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
        Query query = em.createQuery("select coalesce(sum(r.totalDistanceMeters), 0.0) from RunRecord r where r.user.id = :userId");
        query.setParameter("userId", userId);
        return ((Long) query.getSingleResult()).intValue();
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
        Query query = em.createQuery("select count(r) from RunRecord r where r.user.id = :userId and function('YEAR', r.createdAt) = :year and function('MONTH', r.createdAt) = :month");
        query.setParameter("userId", userId);
        query.setParameter("year", yearMonth.getYear());
        query.setParameter("month", yearMonth.getMonth());
        return ((Long) query.getSingleResult()).intValue();
    }

    /**
     * <pre>
     * 특정 사용자(userId)가 특정 연월(yearMonth)에 달린 총 거리를 미터(m) 단위로 조회합니다.
     * yearMonth 를 그대로 where 절에 조건문으로 사용하면 인덱스를 타지 않는다
     * between 으로 사용하면 인덱스를 탄다
     * </pre>
     *
     * @param userId    조회할 사용자의 ID
     * @param yearMonth 조회할 연월 (예: YearMonth.of(2025, 6))
     * @return 해당 사용자의 해당 월 총 달리기 거리 (미터 단위). 기록이 없으면 0을 반환합니다.
     */
    public Integer findTotalDistanceByUserIdAndYearMonth(Integer userId, YearMonth yearMonth) {
        // 1. YearMonth 객체를 사용하여 해당 월의 시작일과 종료일을 계산합니다.
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay(); // 예: 2025-06-01 00:00:00
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59); // 예: 2025-06-30 23:59:59

        // 2. 계산된 시작일과 종료일을 사용하여 BETWEEN 쿼리를 실행합니다.
        Query query = em.createQuery("select coalesce(sum(r.totalDistanceMeters), 0.0) from RunRecord r where r.user.id = :userId and r.createdAt between :startDate and :endDate");
        query.setParameter("userId", userId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return ((Long) query.getSingleResult()).intValue();
    }

    /**
     * 특정 사용자가 특정 기간 동안 달린 거리의 총합을 조회합니다.
     * 'myDistance' 필드를 채우는 데 사용됩니다.
     * [핵심] COALESCE 함수: 달리기 기록이 하나도 없을 경우 합계가 NULL이 되는 것을 방지하고 0.0을 반환해줍니다.
     */
    public Integer findTotalDistanceByUserIdAndDateRange(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        Query query = em.createQuery("select coalesce(sum(r.totalDistanceMeters), 0.0) from RunRecord r where r.user.id = :userId and r.createdAt between :startDate and :endDate");
        query.setParameter("userId", userId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return ((Long) query.getSingleResult()).intValue();
    }

    /**
     * RunRecord 전체 조회
     *
     * @return
     */
    public List<RunRecord> findAllByUserId(Integer userId) {
        Query query = em.createQuery("select r from RunRecord r where r.user.id = : userId order by r.createdAt", RunRecord.class);
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
                "select r from RunRecord r where r.user.id = :userId and r.createdAt between :start and :end order by r.createdAt",
                RunRecord.class);
        query.setParameter("userId", userId);
        query.setParameter("start", start);
        query.setParameter("end", end);
        List<RunRecord> runRecords = query.getResultList();
        return runRecords;
    }

    // 여러 유저용 (새로 추가)
    public List<RunRecord> findAllByCreatedAtBetween(List<Integer> userIds, LocalDateTime start, LocalDateTime end) {
        if (userIds == null || userIds.isEmpty()) return List.of(); // 빈 목록 처리
        Query query = em.createQuery(
                "select r from RunRecord r where r.user.id in :userIds and r.createdAt between :start and :end order by r.createdAt",
                RunRecord.class);
        query.setParameter("userIds", userIds);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }

    /**
     * RunRecord 전체 조회 + 페이징
     *
     * @return
     */
    public List<RunRecord> findAllByUserIdPage(Integer userId, Integer page) {
        Query query = em.createQuery("select r from RunRecord r where r.user.id = : userId", RunRecord.class);
        query.setParameter("userId", userId);
        query.setFirstResult((page - 1) * Constants.RUN_LIST_FETCH_SIZE); // 시작 인덱스
        query.setMaxResults(Constants.RUN_LIST_FETCH_SIZE);         // 페이지당 개수
        List<RunRecord> runRecords = query.getResultList();
        return runRecords;
    }

    /**
     * RunRecord 최신순 조회
     *
     * @return
     */
    public List<RunRecord> findAllByUserIdOrderByCreatedAtDesc(Integer userId, Integer page) {
        Query query = em.createQuery("select r from RunRecord r where r.user.id = : userId  order by r.createdAt desc", RunRecord.class);
        query.setParameter("userId", userId);
        query.setFirstResult((page - 1) * Constants.RUN_LIST_FETCH_SIZE); // 시작 인덱스
        query.setMaxResults(Constants.RUN_LIST_FETCH_SIZE);         // 페이지당 개수
        List<RunRecord> runRecords = query.getResultList();
        return runRecords;
    }

    /**
     * RunRecord 오래된 순 조회
     *
     * @return
     */
    public List<RunRecord> findAllByUserIdOrderByCreatedAtAsc(Integer userId, Integer page) {
        Query query = em.createQuery("select r from RunRecord r where r.user.id = : userId order by r.createdAt asc", RunRecord.class);
        query.setParameter("userId", userId);
        query.setFirstResult((page - 1) * Constants.RUN_LIST_FETCH_SIZE); // 시작 인덱스
        query.setMaxResults(Constants.RUN_LIST_FETCH_SIZE);         // 페이지당 개수
        List<RunRecord> runRecords = query.getResultList();
        return runRecords;
    }

    /**
     * RunRecord 최장거리 순 조회
     *
     * @return
     */
    public List<RunRecord> findAllByUserIdOrderByDistanceDesc(Integer userId, Integer page) {
        Query query = em.createQuery("select r from RunRecord r where r.user.id = : userId order by r.totalDistanceMeters desc", RunRecord.class);
        query.setParameter("userId", userId);
        query.setFirstResult((page - 1) * Constants.RUN_LIST_FETCH_SIZE); // 시작 인덱스
        query.setMaxResults(Constants.RUN_LIST_FETCH_SIZE);         // 페이지당 개수
        List<RunRecord> runRecords = query.getResultList();
        return runRecords;
    }

    /**
     * RunRecord 최단거리 순 조회
     *
     * @return
     */
    public List<RunRecord> findAllByUserIdOrderByDistanceAsc(Integer userId, Integer page) {
        Query query = em.createQuery("select r from RunRecord r where r.user.id = : userId order by r.totalDistanceMeters asc", RunRecord.class);
        query.setParameter("userId", userId);
        query.setFirstResult((page - 1) * Constants.RUN_LIST_FETCH_SIZE); // 시작 인덱스
        query.setMaxResults(Constants.RUN_LIST_FETCH_SIZE);         // 페이지당 개수
        List<RunRecord> runRecords = query.getResultList();
        return runRecords;
    }

    /**
     * RunRecord 최고 페이스 조회
     *
     * @return
     */
    public List<RunRecord> findAllByUserIdOrderByAvgPaceDesc(Integer userId, Integer page) {
        Query query = em.createQuery("select r from RunRecord r where r.user.id = : userId order by r.avgPace desc", RunRecord.class);
        query.setParameter("userId", userId);
        query.setFirstResult((page - 1) * Constants.RUN_LIST_FETCH_SIZE); // 시작 인덱스
        query.setMaxResults(Constants.RUN_LIST_FETCH_SIZE);         // 페이지당 개수
        List<RunRecord> runRecords = query.getResultList();
        return runRecords;
    }

    /**
     * RunRecord 최저 페이스 조회
     *
     * @return
     */
    public List<RunRecord> findAllByUserIdOrderByAvgPaceAsc(Integer userId, Integer page) {
        Query query = em.createQuery("select r from RunRecord r where r.user.id = : userId order by r.avgPace asc", RunRecord.class);
        query.setParameter("userId", userId);
        query.setFirstResult((page - 1) * Constants.RUN_LIST_FETCH_SIZE); // 시작 인덱스
        query.setMaxResults(Constants.RUN_LIST_FETCH_SIZE);         // 페이지당 개수
        List<RunRecord> runRecords = query.getResultList();
        return runRecords;
    }

    /**
     * 페이징 갯수 조회
     *
     * @return
     */
    public Long totalCount(Integer userId) {
        String sql = "select count(r) from RunRecord r where r.user.id = :userId";
        Query query = em.createQuery(sql, Long.class);
        query.setParameter("userId", userId);
        return (Long) query.getSingleResult();
    }
}