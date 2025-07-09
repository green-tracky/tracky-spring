package com.example.tracky.community.challenges.repository;

import com.example.tracky.community.challenges.domain.ChallengeJoin;
import com.example.tracky.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ChallengeJoinRepository {
    private final EntityManager em;

    /**
     * <pre>
     * 특정 유저가 참가한 모든 'ChallengeJoin' 엔티티 목록을 조회합니다.
     * 이 데이터를 기반으로 '참가한 챌린지' 목록을 얻습니다.
     * join fetch
     * - challenge
     * </pre>
     */
    public List<ChallengeJoin> findAllByUserIdJoin(Integer userId) {
        Query query = em.createQuery("select cj from ChallengeJoin cj join fetch cj.challenge c where cj.user.id = :userId", ChallengeJoin.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    /**
     * <pre>
     * [최적화] 특정 유저가 참가한 모든 챌린지의 'ID'만 조회합니다.
     * 이 ID 목록은 '참가하지 않은 챌린지'를 필터링하는 데 효율적으로 사용됩니다.
     * </pre>
     */
    public Set<Integer> findChallengeIdsByUserId(Integer userId) {
        Query query = em.createQuery("select cj.challenge.id from ChallengeJoin cj where cj.user.id = :userId");
        query.setParameter("userId", userId);
        List<Integer> resultList = query.getResultList();
        return new HashSet<>(resultList);
    }

    /**
     * <pre>
     * 특정 챌린지에 참가한 유저 수를 조회합니다.
     * '추천 챌린지'의 참가자 수를 표시하는 데 사용됩니다.
     * </pre>
     */
    public Integer countByChallengeId(Integer challengeId) {
        Query query = em.createQuery("select count(cj) from ChallengeJoin cj where cj.challenge.id = :challengeId");
        query.setParameter("challengeId", challengeId);
        return ((Long) query.getSingleResult()).intValue();
    }

    /**
     * 특정 유저가 특정 챌린지에 참가했는지 여부를 조회합니다.
     */
    public Boolean existsByUserIdAndChallengeId(Integer userId, Integer challengeId) {
        Query query = em.createQuery("select count(cj) from ChallengeJoin cj where cj.user.id = :userId and cj.challenge.id = :challengeId");
        query.setParameter("userId", userId);
        query.setParameter("challengeId", challengeId);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    /**
     * 특정 챌린지에서 특정 유저의 순위를 조회합니다.
     */
    public Integer findRankByChallengeIdAndUserId(Integer challengeId, Integer userId) {
        Query query = em.createNativeQuery(
                """
                        select user_rank
                        from (
                            select
                              cj.user_id,
                              coalesce(sum(r.total_distance_meters), 0) as total_distance,
                              rank() over (order by coalesce(sum(r.total_distance_meters), 0) desc) as user_rank
                            from challenge_join_tb cj
                            join challenge_tb c on c.id = cj.challenge_id
                            left join run_record_tb r
                              on r.user_id = cj.user_id
                              and r.created_at between c.start_date and c.end_date
                            where cj.challenge_id = :challengeId
                            group by cj.user_id
                            having coalesce(sum(r.total_distance_meters), 0) > 0
                        ) ranked
                        where ranked.user_id = :userId
                        """
        );
        query.setParameter("challengeId", challengeId);
        query.setParameter("userId", userId);

        Object result = query.getSingleResult();
        // Number 로 다운캐스팅 하는 이유는 결과의 타입이 Integer, Long, BigDecimal 등 벤더에 따라 다를 수 있기 때문
        return result != null ? ((Number) result).intValue() : null;
    }

    /**
     * 챌린지참여 저장
     *
     * @param challengeJoin
     * @return
     */
    public ChallengeJoin save(ChallengeJoin challengeJoin) {
        em.persist(challengeJoin);
        return challengeJoin;
    }

    /**
     * 내가 참여한 챌린지 조회
     *
     * @param challengeId
     * @param userId
     * @return
     */
    public Optional<ChallengeJoin> findByChallengeIdAndUserId(Integer challengeId, Integer userId) {
        Query query = em.createQuery("select cj from ChallengeJoin cj where cj.user.id = :userId and cj.challenge.id = :challengeId", ChallengeJoin.class);
        query.setParameter("userId", userId);
        query.setParameter("challengeId", challengeId);

        try {
            return Optional.of((ChallengeJoin) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

    /**
     * 내가 참여한 챌린지 나가기
     *
     * @param challengeJoinPS
     */
    public void delete(ChallengeJoin challengeJoinPS) {
        em.remove(challengeJoinPS);
    }

    /**
     * 챌린지에 참여한 모든 유저 조회
     *
     * @param challengeId
     * @return
     */
    public List<User> findUserAllById(Integer challengeId) {
        Query query = em.createQuery("select cj.user from ChallengeJoin cj where cj.challenge.id = :challengeId order by cj.joinDate", User.class);
        query.setParameter("challengeId", challengeId);
        List<User> resultList = query.getResultList();
        return resultList;
    }

    // ...

    /**
     * 참여중인 챌린지 중에서 현재 진행중인 챌린지들만 조회
     *
     * @param userId
     * @return
     */
    public List<ChallengeJoin> findAllByUserIdAndIsInProgressTrue(Integer userId) {
        Query query = em.createQuery("select cj from ChallengeJoin cj join fetch cj.challenge c where cj.user.id = :userId and c.isInProgress = true order by cj.joinDate", ChallengeJoin.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    /**
     * 해당 챌린지에 참여한 사람들 조회
     *
     * @param challengeId
     * @return
     */
    public List<ChallengeJoin> findAllByChallengeId(Integer challengeId) {
        return em.createQuery("select cj from ChallengeJoin cj where cj.challenge.id = :challengeId order by cj.joinDate", ChallengeJoin.class)
                .setParameter("challengeId", challengeId)
                .getResultList();
    }
}
