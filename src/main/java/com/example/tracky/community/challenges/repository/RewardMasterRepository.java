package com.example.tracky.community.challenges.repository;

import com.example.tracky._core.enums.ChallengeTypeEnum;
import com.example.tracky.community.challenges.domain.RewardMaster;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RewardMasterRepository {
    private final EntityManager em;

    /**
     * 사설 챌린지용: type이 사설인 모든 보상
     *
     * @param type
     * @return
     */
    public List<RewardMaster> findAllByType(ChallengeTypeEnum type) {
        Query query = em.createQuery(
                "select r from RewardMaster r where r.challengeType = :type order by r.createdAt", RewardMaster.class);
        query.setParameter("type", type);
        return query.getResultList();
    }

    /**
     * <pre>
     *  공개 챌린지용: rewardName이 챌린지 이름과 동일
     * </pre>
     *
     * @param rewardName
     * @return
     */
    public List<RewardMaster> findAllByRewardName(String rewardName) {
        Query query = em.createQuery(
                "select r from RewardMaster r where r.rewardName = :rewardName", RewardMaster.class);
        query.setParameter("rewardName", rewardName);
        return query.getResultList();
    }

    /**
     * 보상 이름으로 조회
     *
     * @param rewardName
     * @return
     */
    public Optional<RewardMaster> findByRewardName(String rewardName) {
        Query query = em.createQuery(
                "select r from RewardMaster r where r.rewardName = :rewardName", RewardMaster.class);
        query.setParameter("rewardName", rewardName);
        try {
            return Optional.of((RewardMaster) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

    /**
     * 보상 아이디로 조회
     *
     * @param rewardId
     * @return
     */
    public Optional<RewardMaster> findById(Integer rewardId) {
        RewardMaster rewardMaster = em.find(RewardMaster.class, rewardId);
        return Optional.ofNullable(rewardMaster);
    }
}
