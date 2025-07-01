package com.example.tracky.community.like;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class LikeRepository {

    private final EntityManager em;

    public Integer countByPostId(int postId) {
        Query query = em.createQuery("select count(li) from Like li where li.post.id = :postId");
        query.setParameter("postId", postId);

        Long count = (Long) query.getSingleResult();
        return count.intValue();
    }

    public Optional<Like> findByUserIdAndPostId(Integer userId, Integer postId) {
        Query query = em.createQuery("select li from Like li where li.user.id = :userId and li.post.id = :postId", Like.class);
        query.setParameter("userId", userId);
        query.setParameter("postId", postId);
        try {
            Like likePs = (Like) query.getSingleResult();
            return Optional.of(likePs);
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }
}
