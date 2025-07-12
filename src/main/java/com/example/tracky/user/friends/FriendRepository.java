package com.example.tracky.user.friends;

import com.example.tracky.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendRepository {

    private final EntityManager em;

    /**
     * 유저 ID로 친구 목록 조회 (fromUser 또는 toUser인 경우 모두 조회)
     *
     * @param userId 로그인 한 유저의 ID
     * @return List<친구>
     */
    public List<Friend> findfriendByUserIdJoinFriend(Integer userId) {
        Query query = em.createQuery("select f from Friend f join fetch f.fromUser join fetch f.toUser where f.fromUser.id = :id or f.toUser.id = :id", Friend.class);
        query.setParameter("id", userId);
        List<Friend> friends = query.getResultList();
        return friends;
    }

    /**
     * 친구 저장
     *
     * @param friend 친구 객체
     * @return 저장된 친구 객체
     */
    public Friend save(Friend friend) {
        em.persist(friend);
        return friend;
    }

    /**
     * 두 유저가 이미 친구 관계인지 검사
     *
     * @param userA 유저 A
     * @param userB 유저 B
     * @return 친구 관계 여부 (ture or false)
     */
    public Boolean existsFriend(User userA, User userB) {
        Long count = em.createQuery("""
                            select count(f) from Friend f
                            where (f.fromUser.id = :userA and f.toUser.id = :userB)
                               or (f.fromUser.id = :userB and f.toUser.id = :userA)
                        """, Long.class)
                .setParameter("userA", userA.getId())
                .setParameter("userB", userB.getId())
                .getSingleResult();

        return count > 0;
    }

}
