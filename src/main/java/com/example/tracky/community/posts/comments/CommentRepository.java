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

    public List<Comment> findParentComments(Integer postId, Integer page) {

        return em.createQuery(
                        "select c from Comment c where c.post.id = :postId and c.parent is null order by c.id desc",
                        Comment.class)
                .setParameter("postId", postId)
                .setFirstResult((page - 1) * 5)
                .setMaxResults(5)
                .getResultList();
    }

    public List<Comment> findChildCommentsByParentIds(List<Integer> parentIds) {
        if (parentIds == null || parentIds.isEmpty()) {
            return Collections.emptyList();
        }

        return em.createQuery(
                        "select c from Comment c where c.parent.id in :ids order by c.id asc",
                        Comment.class)
                .setParameter("ids", parentIds)
                .getResultList();
    }
}
