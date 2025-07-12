package com.example.tracky.community.posts.comments;


import com.example.tracky._core.constants.Constants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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


    /**
     * @param postId
     * @param page   기본값 1
     * @return
     */

    public List<Comment> findParentComments(Integer postId, Integer page) {

        return em.createQuery(
                        "select c from Comment c where c.post.id = :postId and c.parent is null order by c.id desc",
                        Comment.class)
                .setParameter("postId", postId)
                .setFirstResult((page - 1) * Constants.PAGE_SIZE)
                .setMaxResults(Constants.PAGE_SIZE)
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

    public Comment save(Comment comment) {
        em.persist(comment);
        return comment;
    }

    public Optional<Comment> findById(Integer parentId) {
        return Optional.ofNullable(em.find(Comment.class, parentId));
    }

    public Integer countParentComments(Integer postId) {
        String jpql = """
                    select count(c)
                    from Comment c
                    where c.post.id = :postId and c.parent is null
                """;

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("postId", postId)
                .getSingleResult();

        Integer parentCount = count.intValue();
        return parentCount;
    }


    public Integer countTotalCommentsInPage(Integer postId, Integer page) {
        // 1. 부모 댓글 조회 (페이징)
        String jpql = """
                    select c.id
                    from Comment c
                    where c.post.id = :postId and c.parent is null
                    order by c.id desc
                """;

        List<Integer> parentIds = em.createQuery(jpql, Integer.class)
                .setParameter("postId", postId)
                .setFirstResult((page - 1) * Constants.PAGE_SIZE)
                .setMaxResults(Constants.PAGE_SIZE)
                .getResultList();

        if (parentIds.isEmpty()) {
            return 0;
        }

        // 2. 대댓글 개수 조회

        String childCountJpql = """
                    select count(c)
                    from Comment c
                    where c.parent.id in :parentIds
                """;

        Long childCount = em.createQuery(childCountJpql, Long.class)
                .setParameter("parentIds", parentIds)
                .getSingleResult();

        // 3. 전체 수 = 부모 + 자식
        return parentIds.size() + childCount.intValue();
    }

    public void delete(Comment comment) {
        em.remove(comment);
    }

}
