package com.example.tracky.community.post;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public Post findById(Integer id) {
        return em.find(Post.class, id);
    }

    public void delete(Post post) {
        em.remove(post);
    }

}
