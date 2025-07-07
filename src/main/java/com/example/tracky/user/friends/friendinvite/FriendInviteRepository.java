package com.example.tracky.user.friends.friendinvite;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FriendInviteRepository {

    private final EntityManager em;

    public FriendInvite save(FriendInvite invite) {
        em.persist(invite);
        return invite;
    }
}
