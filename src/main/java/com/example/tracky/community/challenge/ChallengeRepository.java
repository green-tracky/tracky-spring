package com.example.tracky.community.challenge;

import java.util.List;

import com.example.tracky.runrecord.runbadge.RunBadge;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChallengeRepository {

    private final EntityManager em;

    public List<Challenge> findAll() {
        return em.createQuery("select c from Challenge c", Challenge.class)
                .getResultList();
    }

}