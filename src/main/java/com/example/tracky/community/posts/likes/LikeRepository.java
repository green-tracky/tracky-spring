package com.example.tracky.community.posts.likes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class LikeRepository {

    private final EntityManager em;

    public Integer countByPostId(Integer postId) {
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

    public Like save(Like like) {
        em.persist(like);
        return like;
    }

//    public Integer findByPostId(int postId) {
//        Query query = em.createQuery("select count(li) from Like li where li.post.id = :postId");
//        query.setParameter("postId", postId);
//
//        Long count = (Long) query.getSingleResult();
//        return count.intValue();
//    }

    public Optional<Like> findById(Integer id) {
        return Optional.ofNullable(em.find(Like.class, id));
    }

    public void deleteById(Integer id) {
        Query query = em.createQuery("delete from Like li where li.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }


}
