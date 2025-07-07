package com.example.tracky.user.friends;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendRepository {

    private final EntityManager em;

    public List<Friend> findfriendByUserIdJoinFriend(Integer userId) {
        Query query = em.createQuery("select f from Friend f join fetch User uf on f.fromUser.id = uf.id join fetch User ut on f.toUser.id = ut.id where f.fromUser.id = :id or f.toUser.id = :id", Friend.class);
        query.setParameter("id", userId);
        List<Friend> friends = query.getResultList();
        return friends;
    }
}
