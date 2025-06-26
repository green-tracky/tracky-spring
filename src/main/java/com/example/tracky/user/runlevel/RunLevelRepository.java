package com.example.tracky.user.runlevel;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RunLevelRepository {

    private final EntityManager em;

    public List<RunLevel> findAll() {
        return em.createQuery("select r from RunLevel r", RunLevel.class)
                .getResultList();
    }

}
