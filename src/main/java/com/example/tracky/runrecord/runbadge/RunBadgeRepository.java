package com.example.tracky.runrecord.runbadge;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RunBadgeRepository {

    private final EntityManager em;

    public List<RunBadge> findAllBadge() {
        return em.createQuery("select b from Badge b", RunBadge.class)
                .getResultList();
    }

}