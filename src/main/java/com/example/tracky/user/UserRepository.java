package com.example.tracky.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    /**
     * 나중에 findByIdJoin 으로 바뀌어야함
     * <p>
     * RunLevel 도 같이 가져와야함
     *
     * @param userId
     * @return
     */
    public Optional<User> findById(Integer userId) {
        Query query = em.createQuery("select u from User u where u.id = :id", User.class);
        query.setParameter("id", userId);
        try {
            return Optional.of((User) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }
}
