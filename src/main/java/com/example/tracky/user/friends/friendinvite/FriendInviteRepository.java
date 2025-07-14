package com.example.tracky.user.friends.friendinvite;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.enums.InviteStatusEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public List<FriendInvite> findAllByToUserIdJoin(Integer userid) {
        Query query = em.createQuery("select fi from FriendInvite fi join fetch fi.fromUser where fi.toUser.id = :id and fi.status = :status");
        query.setParameter("id", userid);
        query.setParameter("status", InviteStatusEnum.PENDING);
        List<FriendInvite> inviteList = query.getResultList();
        return inviteList;
    }

    /**
     * 본인에게 들어온 친구 요청인지 확인
     *
     * @param inviteId 친구 요청 ID
     * @param userId   로그인한 유저 ID
     * @return 친구 요청 객체
     */
    public Optional<FriendInvite> findValidateByInviteId(Integer inviteId, Integer userId) {
        try {
            return Optional.ofNullable(em.createQuery("select f from FriendInvite f where f.id = :inviteId and f.toUser.id = :userId ", FriendInvite.class)
                    .setParameter("inviteId", inviteId)
                    .setParameter("userId", userId)
                    .getSingleResult());
        } catch (RuntimeException e) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }
    }

    /**
     * 중복 요청 방지
     *
     * @param fromUser 요청 보낸 유저
     * @param toUser   요청 받은 유저
     * @return 존재 여부
     */
    public Boolean existsWaitingInvite(User fromUser, User toUser) {
        Long count = em.createQuery("""
                            select count(f) from FriendInvite f
                            where f.fromUser.id = :fromId
                            and f.toUser.id = :toId
                            and (f.status = 'PENDING' or f.status = 'ACCEPTED')
                        """, Long.class)
                .setParameter("fromId", fromUser.getId())
                .setParameter("toId", toUser.getId())
                .getSingleResult();

        return count > 0;
    }
}
