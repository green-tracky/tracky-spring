package com.example.tracky.community.posts.comments;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
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

    public List<Comment> findParentComments(Integer postId, int page) {
        if (page < 0) page = 0;
        return em.createQuery(
                        "SELECT c FROM Comment c JOIN FETCH c.post WHERE c.post.id = :postId AND c.parent IS NULL ORDER BY c.id DESC",
                        Comment.class)
                .setParameter("postId", postId)
                .setFirstResult(page * 5)
                .setMaxResults(5)
                .getResultList();
    }

    public List<Comment> findChildCommentsByParentIds(List<Integer> parentIds) {
        if (parentIds == null || parentIds.isEmpty()) {
            return Collections.emptyList();
        }

        return em.createQuery(
                        "SELECT c FROM Comment c join fetch c.post WHERE c.parent.id IN :ids ORDER BY c.id ASC",
                        Comment.class)
                .setParameter("ids", parentIds)
                .getResultList();
    }
}
