package com.example.tracky.community.posts.comments;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CommentRepository {

    private final EntityManager em;

    public Integer countByPostId(Integer postId) {
        Query query = em.createQuery("select count(c) from Comment c where c.post.id = :postId");
        query.setParameter("postId", postId);

        Long count = (Long) query.getSingleResult();
        return count.intValue();
    }

    public List<Comment> findByPostId(Integer postId, int page) {

        Query query = em.createQuery(
                        "SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL ORDER BY c.id DESC", Comment.class)
                .setParameter("postId", postId)
                .setFirstResult(page * 5)
                .setMaxResults(5)
                .getResultList();
    }
}
