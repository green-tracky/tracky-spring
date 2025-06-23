package com.example.tracky.runrecord.runbadge;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RunBadgeRepository {

    private final EntityManager em;

    public List<RunBadge> findAll() {
        return em.createQuery("select b from RunBadge b", RunBadge.class)
                .getResultList();
    }

}