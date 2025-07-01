package com.example.tracky.community.challenge.repository;

import com.example.tracky.community.challenge.domain.ChallengeJoin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
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
        Query query = em.createQuery("select cj.challenge.id from ChallengeJoin cj where cj.user.id = :userId", Integer.class);
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
        Query query = em.createQuery("select count(cj) from ChallengeJoin cj where cj.challenge.id = :challengeId", Long.class);
        query.setParameter("challengeId", challengeId);
        return ((Long) query.getSingleResult()).intValue();
    }
}
