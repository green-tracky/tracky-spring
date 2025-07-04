package com.example.tracky.runrecord.pictures;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PictureRepository {

    private final EntityManager em;

}