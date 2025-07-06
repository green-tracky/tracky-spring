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

    public List<UserChallengeReward> findAllByUserId(Integer userId) {
        Query query = em.createQuery("select u from UserChallengeReward u where u.user.id = :userId", UserChallengeReward.class);
        query.setParameter("userId", userId);
        List<UserChallengeReward> userChallengeRewards = query.getResultList();
        return userChallengeRewards;
    }

}
