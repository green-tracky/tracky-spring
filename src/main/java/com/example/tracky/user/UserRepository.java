package com.example.tracky.user;

import com.example.tracky.user.enums.UserTypeEnum;
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
}
