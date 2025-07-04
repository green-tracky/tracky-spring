package com.example.tracky.runrecord.runbadges.runbadgeachvs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * RunBadgeAchv(뱃지 획득 내역) 엔티티에 대한 DB 접근을 담당합니다.
 * 뱃지 획득 내역의 저장, 삭제, 조회 기능을 제공합니다.
 * </pre>
 */
@Repository
@RequiredArgsConstructor
public class RunBadgeAchvRepository {

    // JPA의 영속성 컨텍스트를 관리하고 데이터베이스 작업을 수행하는 EntityManager
    private final EntityManager em;

    /**
     * 뱃지 획득 내역(RunBadgeAchv)을 데이터베이스에 저장합니다.
     *
     * @param runBadgeAchv 저장할 뱃지 획득 내역 엔티티
     */
    public RunBadgeAchv save(RunBadgeAchv runBadgeAchv) {
        em.persist(runBadgeAchv);
        return runBadgeAchv;
    }

    /**
     * <pre>
     * 뱃지 획득 내역(RunBadgeAchv)을 데이터베이스에서 삭제합니다.
     * 영속성 컨텍스트에 없는 엔티티(detached)의 경우, merge 후 삭제하여 안정성을 높입니다.
     * </pre>
     *
     * @param runBadgeAchv 삭제할 뱃지 획득 내역 엔티티
     */
    public void delete(RunBadgeAchv runBadgeAchv) {
        em.remove(runBadgeAchv);
    }

    /**
     * <pre>
     * 특정 사용자가 특정 뱃지를 보유하고 있는지 조회합니다.
     * '최고 기록'이나 '최초 달성' 뱃지처럼 한 번만 획득해야 하는 뱃지의 중복 부여를 방지하는 데 사용됩니다.
     * </pre>
     *
     * @param runBadgeId 검사할 뱃지
     * @param userId     검사할 사용자
     * @return 조회된 뱃지 획득 내역을 담은 Optional 객체.
     */
    public Optional<RunBadgeAchv> findByRunBadgeIdAndUserId(Integer runBadgeId, Integer userId) {
        Query query = em.createQuery(
                "SELECT rba FROM RunBadgeAchv rba WHERE rba.runBadge.id = :runBadgeId AND rba.user.id = :userId", RunBadgeAchv.class);
        query.setParameter("runBadgeId", runBadgeId);
        query.setParameter("userId", userId);

        try {
            return Optional.of((RunBadgeAchv) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

    /**
     * <pre>
     * 특정 사용자가 특정 연월에 특정 뱃지를 획득했는지 확인합니다.
     * '이달의 챌린지'처럼 월별로 획득 여부를 판단해야 하는 뱃지의 중복 부여를 방지하는 데 사용됩니다.
     * </pre>
     *
     * @param userId     검사할 사용자
     * @param runBadgeId 검사할 뱃지
     * @param yearMonth  검사할 연월
     * @return 해당 연월에 획득 내역이 존재하면 true, 아니면 false
     */
    public Boolean existsByUserIdAndRunBadgeIdAndYearMonth(Integer userId, Integer runBadgeId, YearMonth yearMonth) {
        Long count = em.createQuery("select count (rba) from RunBadgeAchv rba where rba.user.id = :userId and rba.runBadge.id = :runBadgeId and function('YEAR', rba.achievedAt) = :year and function('MONTH', rba.achievedAt) = :month", Long.class)
                .setParameter("userId", userId)
                .setParameter("runBadgeId", runBadgeId)
                .setParameter("year", yearMonth.getYear())
                .setParameter("month", yearMonth.getMonthValue())
                .getSingleResult();
        return count > 0;
    }

    /**
     * <pre>
     * 해당 유저가 획득한 뱃지 리스트
     * join fetch
     * - runRecord
     * - runBadge
     * </pre>
     *
     * @param userId
     * @return
     */
    public List<RunBadgeAchv> findByUserIdJoin(Integer userId) {
        Query query = em.createQuery("select rba from RunBadgeAchv rba join fetch rba.runRecord r join fetch rba.runBadge b  where rba.user.id = :userId", RunBadgeAchv.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

}
