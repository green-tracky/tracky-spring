package com.example.tracky.community.post;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final EntityManager em;

    public List<Post> findAllWithRunRecord() {
        return em.createQuery("""
                        SELECT p FROM Post p 
                        JOIN FETCH p.user 
                        LEFT JOIN FETCH p.runRecord
                        """, Post.class)
                .getResultList();
    }


}
