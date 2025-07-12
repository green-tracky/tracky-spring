package com.example.tracky.community.challenges.repository;

import com.example.tracky.community.challenges.domain.UserChallengeReward;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserChallengeRewardRepository {
    private final EntityManager em;

    /**
     * 해당 유저가 받은 모든 보상 목록
     *
     * @param userId
     * @return
     */
    public List<UserChallengeReward> findAllByUserId(Integer userId) {
        Query query = em.createQuery("select u from UserChallengeReward u where u.user.id = :userId", UserChallengeReward.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    /**
     * 해당 유저가 해당 공개챌린지에서 해당 챌린지 보상을 받은 적이 있는가
     *
     * @param userId
     * @param challengeId
     * @param challengeName
     * @return
     */
    public Boolean existsPublicRewardByChallengeName(Integer userId, Integer challengeId, String challengeName) {
        // 공개 챌린지 중복 확인 쿼리
        // 유저, 챌린지가 일치하고, 보상의 이름(rewardName)이 챌린지의 이름(name)과 같은 기록이 있는지 확인
        Long count = em.createQuery("select count(ucr) from UserChallengeReward ucr join ucr.rewardMaster rm where ucr.user.id = :userId and ucr.challenge.id = :challengeId and rm.rewardName = :challengeName", Long.class)
                .setParameter("userId", userId)
                .setParameter("challengeId", challengeId)
                .setParameter("challengeName", challengeName)
                .getSingleResult();

        return count > 0;
    }

    /**
     * 해당 유저가 해당 사설챌린지에서 해당 보상 아이디로 보상을 받은 적이 있는가
     *
     * @param userId
     * @param challengeId
     * @param rewardId
     * @return
     */
    public Boolean existsPrivateRewardByRewardId(Integer userId, Integer challengeId, Integer rewardId) {
        Long count = em.createQuery("select count(ucr) from UserChallengeReward ucr join ucr.rewardMaster rm where ucr.user.id = :userId and ucr.challenge.id = :challengeId and rm.id = :rewardId", Long.class)
                .setParameter("userId", userId)
                .setParameter("challengeId", challengeId)
                .setParameter("rewardId", rewardId)
                .getSingleResult();

        return count > 0;
    }

    public UserChallengeReward save(UserChallengeReward newReward) {
        em.persist(newReward);
        return newReward;
    }
}
