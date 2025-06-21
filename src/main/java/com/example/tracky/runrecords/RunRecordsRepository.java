package com.example.tracky.runrecords;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RunRecordsRepository {

    private final EntityManager em;

}