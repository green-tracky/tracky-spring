package com.example.tracky.runrecord;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RunRecordRepository {

    private final EntityManager em;

}