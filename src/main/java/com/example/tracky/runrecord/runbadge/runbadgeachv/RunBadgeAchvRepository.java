package com.example.tracky.runrecord.runbadge.runbadgeachv;

import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.Optional;

/**
 * RunBadgeAchv(뱃지 획득 내역) 엔티티에 대한 DB 접근을 담당합니다.
 * 뱃지 획득 내역의 저장, 삭제, 조회 기능을 제공합니다.
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
     * 뱃지 획득 내역(RunBadgeAchv)을 데이터베이스에서 삭제합니다.
     * 영속성 컨텍스트에 없는 엔티티(detached)의 경우, merge 후 삭제하여 안정성을 높입니다.
     *
     * @param runBadgeAchv 삭제할 뱃지 획득 내역 엔티티
     */
    public void delete(RunBadgeAchv runBadgeAchv) {
        em.remove(runBadgeAchv);
    }

    /**
     * 특정 사용자가 특정 뱃지를 보유하고 있는지 조회합니다.
     * '최고 기록'이나 '최초 달성' 뱃지처럼 한 번만 획득해야 하는 뱃지의 중복 부여를 방지하는 데 사용됩니다.
     *
     * @param runBadge 검사할 뱃지
     * @param user     검사할 사용자
     * @return 조회된 뱃지 획득 내역을 담은 Optional 객체.
     */
    public Optional<RunBadgeAchv> findByRunBadgeAndUser(RunBadge runBadge, User user) {
        Query query = em.createQuery(
                "SELECT rba FROM RunBadgeAchv rba WHERE rba.runBadge = :runBadge AND rba.user = :user", RunBadgeAchv.class);
        query.setParameter("runBadge", runBadge);
        query.setParameter("user", user);

        try {
            return Optional.of((RunBadgeAchv) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

    /**
     * 특정 사용자가 특정 연월에 특정 뱃지를 획득했는지 확인합니다.
     * '이달의 챌린지'처럼 월별로 획득 여부를 판단해야 하는 뱃지의 중복 부여를 방지하는 데 사용됩니다.
     *
     * @param user      검사할 사용자
     * @param runBadge  검사할 뱃지
     * @param yearMonth 검사할 연월
     * @return 해당 연월에 획득 내역이 존재하면 true, 아니면 false
     */
    public boolean existsByUserAndBadgeAndYearMonth(User user, RunBadge runBadge, YearMonth yearMonth) {
        Long count = em.createQuery("select count (rba) from RunBadgeAchv rba where rba.user = :user and rba.runBadge = :runBadge and function('YEAR', rba.achievedAt) = :year and function('MONTH', rba.achievedAt) = :month", Long.class)
                .setParameter("user", user)
                .setParameter("runBadge", runBadge)
                .setParameter("year", yearMonth.getYear())
                .setParameter("month", yearMonth.getMonthValue())
                .getSingleResult();
        return count > 0;
    }
}
