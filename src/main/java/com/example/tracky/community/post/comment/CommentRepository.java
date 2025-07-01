package com.example.tracky.community.post.comment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CommentRepository {

    private final EntityManager em;

    public Long findByPostId(int postId) {
        Query query = em.createQuery("select count(c) from Comment c where c.post.id = :postId");
        query.setParameter("postId", postId);

        Long count = (Long) query.getSingleResult();
        return count;
    }
}
