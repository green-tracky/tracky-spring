package com.example.tracky.runrecord.picture;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PictureRepository {

    private final EntityManager em;

}