package com.example.tracky.community.challenge.repository;

import com.example.tracky.community.challenge.domain.RewardMaster;
import com.example.tracky.community.challenge.enums.ChallengeTypeEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RewardMasterRepository {
    private final EntityManager em;

    // 사설 챌린지용: type이 사설인 모든 보상
    public List<RewardMaster> findAllByType(ChallengeTypeEnum type) {
        Query query = em.createQuery(
                "select r from RewardMaster r where r.type = :type", RewardMaster.class);
        query.setParameter("type", type);
        return query.getResultList();
    }

    // 공개 챌린지용: rewardName이 챌린지 이름과 동일
    public List<RewardMaster> findAllByRewardName(String rewardName) {
        Query query = em.createQuery(
                "select r from RewardMaster r where r.rewardName = :rewardName", RewardMaster.class);
        query.setParameter("rewardName", rewardName);
        return query.getResultList();
    }
}
