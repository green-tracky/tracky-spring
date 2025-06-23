package com.example.tracky.runrecord.runbadge;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RunBadgeRepository {

    private final EntityManager em;

}