package com.example.tracky.community.posts;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final EntityManager em;

    public List<Post> findAllJoinRunRecord() {
        return em.createQuery("""
                        select p from Post p 
                        join fetch p.user 
                        left join fetch p.runRecord
                        """, Post.class)
                .getResultList();
    }

    public Post save(Post post) {
        em.persist(post);
        return post;
    }


    public void delete(Post post) {
        em.remove(post);
    }

    public Optional<Post> findById(Integer id) {
        Post postPS = em.find(Post.class, id);
        return Optional.ofNullable(postPS);
    }
}
