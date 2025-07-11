package com.example.tracky.user;

import com.example.tracky._core.enums.UserTypeEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    /**
     * <pre>
     * join fetch
     * - RunLevel
     * </pre>
     *
     * @param userId
     * @return
     */
    public Optional<User> findByIdJoin(Integer userId) {
        Query query = em.createQuery("select u from User u join fetch u.runLevel where u.id = :id", User.class);
        query.setParameter("id", userId);
        try {
            return Optional.of((User) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

    /**
     * <pre>
     * 관리자 계정 찾기
     * 관리자 계정이 2개 이상이면 null 응답함
     * </pre>
     *
     * @return
     */
    public Optional<User> findAdmin() {
        Query query = em.createQuery("select u from User u where u.userType = :userType", User.class);
        query.setParameter("userType", UserTypeEnum.ADMIN);
        try {
            return Optional.of((User) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

    public Optional<User> findById(Integer userId) {
        return Optional.ofNullable(em.find(User.class, userId));
    }

    /**
     * 토큰으로 db 에서 user 조회할 때 사용
     *
     * @param loginId
     * @return
     */
    public Optional<User> findByLoginId(String loginId) {
        Query query = em.createQuery("select u from User u where u.loginId = :loginId", User.class);
        query.setParameter("loginId", loginId);
        try {
            return Optional.of((User) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

    public List<String> findAllUserTag() {
        Query query = em.createQuery("select u.userTag from User u", String.class);
        return query.getResultList();
    }

    public User save(User user) {
        em.persist(user);
        return user;
    }

    public List<User> findByUserTag(String tag) {
        Query query = em.createQuery("select u from User u where upper(u.userTag) like upper(:tag) ");
        query.setParameter("tag", tag + "%");
        List<User> users = query.getResultList();
        return users;
    }

    public void flush() {
        em.flush();
    }

    public void delete(User user) {
        em.remove(user);
    }
}
