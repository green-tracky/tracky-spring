package com.example.tracky.community.challenges.repository;

import com.example.tracky.community.challenges.domain.ChallengeInvite;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChallengeInviteRepository {

    private final EntityManager em;

    public ChallengeInvite save(ChallengeInvite challengeInvite) {
        em.persist(challengeInvite);
        return challengeInvite;
    }
}