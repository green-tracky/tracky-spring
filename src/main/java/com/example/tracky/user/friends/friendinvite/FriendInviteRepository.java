package com.example.tracky.user.friends.friendinvite;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendInviteRepository {

    private final EntityManager em;

    public FriendInvite save(FriendInvite invite) {
        em.persist(invite);
        return invite;
    }

    public List<FriendInvite> findAllByUserId(Integer userid) {
        Query query = em.createQuery("select f from FriendInvite f where f.toUser.id = :id");
        query.setParameter("id", userid);
        List<FriendInvite> inviteList = query.getResultList();
        return inviteList;
    }
}
