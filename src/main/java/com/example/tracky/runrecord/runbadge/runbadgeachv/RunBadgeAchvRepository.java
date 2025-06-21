package com.example.tracky.runrecord.runbadge.runbadgeachv;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RunBadgeAchvRepository {

    private final EntityManager em;

}