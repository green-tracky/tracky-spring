package com.example.tracky.runrecord.runbadges;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RunBadgeRepository {

    private final EntityManager em;

    /**
     * 모든 뱃지 조회
     *
     * @return
     */
    public List<RunBadge> findAll() {
        return em.createQuery("select b from RunBadge b", RunBadge.class)
                .getResultList();
    }

}