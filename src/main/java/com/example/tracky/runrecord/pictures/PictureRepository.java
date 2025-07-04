package com.example.tracky.runrecord.pictures;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PictureRepository {

    private final EntityManager em;

    public List<Picture> findAllById(List<Integer> ids) {
        return em.createQuery("""
                        SELECT p 
                        FROM Picture p 
                        WHERE p.id IN :ids
                        """, Picture.class)
                .setParameter("ids", ids)
                .getResultList();
    }
}