package com.example.tracky.runrecord.runsegment.runcoordinate;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RunCoordinateRepository {

    private final EntityManager em;

}