package com.example.tracky.community.challenges.repository;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky.community.challenges.domain.ChallengeInvite;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChallengeInviteRepository {

    private final EntityManager em;

    public ChallengeInvite save(ChallengeInvite challengeInvite) {
        em.persist(challengeInvite);
        return challengeInvite;
    }

    /**
     * 본인에게 들어온 친구 요청인지 확인
     *
     * @param inviteId 친구 요청 ID
     * @param userId   로그인한 유저 ID
     * @return 친구 요청 객체
     */
    public Optional<ChallengeInvite> findValidateByInviteId(Integer inviteId, Integer userId) {
        try {
            return Optional.ofNullable(em.createQuery("select f from FriendInvite f where f.id = :inviteId and f.toUser.id = :userId ", ChallengeInvite.class)
                    .setParameter("inviteId", inviteId)
                    .setParameter("userId", userId)
                    .getSingleResult());
        } catch (RuntimeException e) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }
    }

    public boolean existsByFromUserIdAndToUserIdAndChallengeId(Integer challengeId, Integer fromUserId, Integer toUserId) {
        Long count = em.createQuery("""
                            select count(ci) from ChallengeInvite ci
                            where ci.fromUser.id = :fromUserId
                            and ci.toUser.id = :toUserId
                            and ci.challenge.id = :challengeId
                            and ci.status = 'PENDING'
                        
                        """, Long.class)
                .setParameter("fromUserId", fromUserId)
                .setParameter("toUserId", toUserId)
                .setParameter("challengeId", challengeId)
                .getSingleResult();

        return count > 0;

    }
}