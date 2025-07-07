package com.example.tracky.community.challenges.repository;

import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.enums.ChallengeTypeEnum;
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
            Query query = em.createQuery("select c from Challenge c where c.endDate > :now and c.type = :type", Challenge.class);
            query.setParameter("now", now);
            query.setParameter("type", ChallengeTypeEnum.PUBLIC);
            return query.getResultList();
        }

        Query query = em.createQuery("select c from Challenge c where c.id not in :joinedChallengeIds and c.endDate > :now and c.type = :type", Challenge.class);
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

}