package com.example.tracky.community.challenges.repository;

import com.example.tracky._core.enums.ChallengeTypeEnum;
import com.example.tracky._core.enums.PeriodTypeEnum;
import com.example.tracky.community.challenges.domain.Challenge;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ChallengeRepository {

    private final EntityManager em;

    /**
     * <pre>
     * 사용자가 참가하지 않았고, 아직 진행 중인 '공개 챌린지' 목록을 조회
     * '참여하기' 목록을 만드는 데 사용
     *
     * where 문에 isInProgress 를 사용하지 않는 이유
     * - 만약 이 스케줄러가 1분이라도 늦게 돌거나, 에러로 인해 실패한다면 그 시간 동안 데이터는 **'오염된 상태(Stale Data)'**가 되기 때문
     * - 데이터의 정확성이 매우 중요한 핵심 로직에서는, 스케줄러의 성공 여부에 의존하는 isInProgress 필드보다, 항상 정확한 '진실의 원천(Single Source of Truth)'인 endDate를 직접 비교하는 것이 훨씬 더 안정적이고 올바른 설계 라고 함
     * </pre>
     */
    public List<Challenge> findUnjoinedPublicChallenges(Set<Integer> joinedChallengeIds, LocalDateTime now) {
        // 참가한 챌린지가 없을 경우 NOT IN 절에서 에러가 날 수 있으므로 분기 처리합니다.
        if (joinedChallengeIds == null || joinedChallengeIds.isEmpty()) {
            Query query = em.createQuery("select c from Challenge c where c.endDate > :now and c.type = :type order by c.createdAt", Challenge.class);
            query.setParameter("now", now);
            query.setParameter("type", ChallengeTypeEnum.PUBLIC);
            return query.getResultList();
        }

        Query query = em.createQuery("select c from Challenge c where c.id not in :joinedChallengeIds and c.endDate > :now and c.type = :type order by c.createdAt", Challenge.class);
        query.setParameter("joinedChallengeIds", joinedChallengeIds);
        query.setParameter("now", now);
        query.setParameter("type", ChallengeTypeEnum.PUBLIC);
        return query.getResultList();
    }

    /**
     * <pre>
     * 챌린지 ID로 상세 정보를 조회
     * </pre>
     */
    public Optional<Challenge> findById(Integer id) {
        return Optional.ofNullable(em.find(Challenge.class, id));
    }

    /**
     * 진행중이면서 종료날짜가 지나간 챌린지들 조회
     *
     * @param now
     * @return
     */
    public List<Challenge> findAllByIsInProgressTrueAndEndDateBefore(LocalDateTime now) {
        Query query = em.createQuery("select c from Challenge c where c.isInProgress = true and c.endDate < :now", Challenge.class);
        query.setParameter("now", now);
        return query.getResultList();
    }

    /**
     * <pre>
     * 특정 년, 월, 주, 거리, 타입까지 확인해서 그 챌린지가 존재하는지 확인하는 쿼리
     * 주간 챌린지 중복 확인용
     * </pre>
     *
     * @param year
     * @param month
     * @param week
     * @param distance
     * @param periodType
     * @return
     */
    public Boolean existsByYearAndMonthAndWeekAndPeriod(int year, int month, int week, int distance, PeriodTypeEnum periodType) {
        Long count = em.createQuery("select count(c) from Challenge c where c.challengeYear = :year and c.challengeMonth = :month and c.weekOfMonth = :week and c.targetDistance = :distance and c.periodType = :periodType", Long.class)
                .setParameter("year", year)
                .setParameter("month", month)
                .setParameter("week", week)
                .setParameter("distance", distance)
                .setParameter("periodType", periodType)
                .getSingleResult();

        return count > 0;
    }

    public Challenge save(Challenge challenge) {
        em.persist(challenge);
        return challenge;
    }

    /**
     * <pre>
     * 특정 년, 월, 거리, 타입까지 확인해서 그 챌린지가 존재하는지 확인하는 쿼리
     * 월간 챌린지 중복 확인용
     * </pre>
     *
     * @param year
     * @param month
     * @param distance
     * @param periodType
     * @return
     */
    public Boolean existsByYearAndMonthAndPeriod(int year, int month, int distance, PeriodTypeEnum periodType) {
        Long count = em.createQuery("select count (c) from Challenge c where c.challengeYear = :year and c.challengeMonth = :month and c.targetDistance = :distance and c.periodType = :periodType", Long.class)
                .setParameter("year", year)
                .setParameter("month", month)
                .setParameter("distance", distance)
                .setParameter("periodType", periodType)
                .getSingleResult();

        return count > 0;
    }

    /**
     * 참가자가 없는 사설 챌린지들 조회
     *
     * @return
     */
    public List<Challenge> findOngoingEmptyPrivateChallenges() {
        // 서브쿼리를 사용하여, ChallengeJoin 테이블에 존재하지 않는 챌린지만을 선택합니다.
        Query query = em.createQuery("select c from Challenge c where c.type = :type and c.isInProgress = true and c.id not in (select distinct cj.challenge.id from ChallengeJoin cj)", Challenge.class);
        query.setParameter("type", ChallengeTypeEnum.PRIVATE);
        return query.getResultList();
    }

    /**
     * 챌린지 아이디 묶음으로 삭제
     *
     * @param challenges 삭제할 챌린지 목록
     */
    public void deleteAllEmptyChallenge(List<Challenge> challenges) {
        // 1. 삭제할 대상이 없으면 즉시 종료하여 불필요한 작업을 방지
        if (challenges == null || challenges.isEmpty()) {
            return;
        }

        // 2. 전달받은 챌린지 목록에서 ID만 추출
        List<Integer> challengeIds = challenges.stream()
                .map(challenge -> challenge.getId())
                .toList();

        // 3. JPQL의 IN 절을 사용하여, ID 목록에 해당하는 모든 챌린지를 삭제하는 쿼리를 작성
        em.createQuery("delete from Challenge c where c.id in :ids")
                .setParameter("ids", challengeIds)
                .executeUpdate();
    }

    // 주간 챌린지 조회
    public List<Challenge> findByYearAndMonthAndWeekOfMonthAndPeriodType(
            int year, int month, int weekOfMonth, PeriodTypeEnum periodType) {
        return em.createQuery("select c from Challenge c where c.challengeYear = :year and c.challengeMonth = :month and c.weekOfMonth = :weekOfMonth and c.periodType = :periodType order by c.createdAt", Challenge.class)
                .setParameter("year", year)
                .setParameter("month", month)
                .setParameter("weekOfMonth", weekOfMonth)
                .setParameter("periodType", periodType)
                .getResultList();
    }

    // 월간 챌린지 조회 (weekOfMonth 조건 없음)
    public List<Challenge> findByYearAndMonthAndPeriodType(
            int year, int month, PeriodTypeEnum periodType) {
        return em.createQuery("select c from Challenge c where c.challengeYear = :year and c.challengeMonth = :month and c.periodType = :periodType order by c.createdAt", Challenge.class)
                .setParameter("year", year)
                .setParameter("month", month)
                .setParameter("periodType", periodType)
                .getResultList();
    }


}