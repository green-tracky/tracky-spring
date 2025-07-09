package com.example.tracky.user.friends.friendinvite;

import com.example.tracky.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendInviteRepository {

    private final EntityManager em;

    /**
     * 친구 요청 저장
     *
     * @param invite 친구 요청 객체
     * @return 저장된 친구 요청
     */
    public FriendInvite save(FriendInvite invite) {
        em.persist(invite);
        return invite;
    }

    /**
     * 로그인한 유저가 받은 친구 요청 목록 조회
     * - 요청 보낸 유저(fromUser)와 함께 fetch join
     *
     * @param userid 로그인한 유저의 ID
     * @return 친구 요청 리스트
     */
    public List<FriendInvite> findAllByUserId(Integer userid) {
        Query query = em.createQuery("select f from FriendInvite f join fetch f.fromUser where f.toUser.id = :id");
        query.setParameter("id", userid);
        List<FriendInvite> inviteList = query.getResultList();
        return inviteList;
    }

    /**
     * 친구 요청 단건 조회 + 유효성 검사 (toUser.id가 일치해야 함)
     *
     * @param inviteId 친구 요청 ID
     * @param userId   로그인한 유저 ID
     * @return 친구 요청 객체
     */
    public FriendInvite findValidateByInviteId(Integer inviteId, Integer userId) {
        return em.createQuery("select f from FriendInvite f where f.id = :inviteId and f.toUser.id = :userId ", FriendInvite.class)
                .setParameter("inviteId", inviteId)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    /**
     * 중복 요청 방지
     *
     * @param fromUser 요청 보낸 유저
     * @param toUser   요청 받은 유저
     * @return 존재 여부
     */
    public boolean existsWaitingInvite(User fromUser, User toUser) {
        Long count = em.createQuery("""
                            select count(f) from FriendInvite f
                            where f.fromUser.id = :fromId
                            and f.toUser.id = :toId
                            and f.status = 'WAITING'
                        """, Long.class)
                .setParameter("fromId", fromUser.getId())
                .setParameter("toId", toUser.getId())
                .getSingleResult();

        return count > 0;
    }
}
