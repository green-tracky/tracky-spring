package com.example.tracky.runrecord.runsegments;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RunSegmentRepository {

    private final EntityManager em;

}