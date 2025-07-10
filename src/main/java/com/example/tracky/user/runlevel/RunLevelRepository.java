package com.example.tracky.user.runlevel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RunLevelRepository {

    private final EntityManager em;

    public List<RunLevel> findAll() {
        return em.createQuery("select r from RunLevel r", RunLevel.class)
                .getResultList();
    }

    /**
     * 모든 레벨 정보를 'sortOrder' 기준으로 내림차순 정렬하여 조회
     * 레벨업 조건을 확인할 때, 가장 높은 레벨부터 검사하여 효율적으로 사용자의 현재 레벨을 찾기 위함
     *
     * @return sortOrder 기준으로 내림차순 정렬된 RunLevel 리스트
     */
    public List<RunLevel> findAllByOrderBySortOrderDesc() {
        Query query = em.createQuery("select rl from RunLevel rl order by rl.sortOrder desc", RunLevel.class);
        return query.getResultList();
    }

    /**
     * 모든 레벨 정보를 'sortOrder' 기준으로 오름차순 정렬하여 조회
     * 레벨 목록 순서대로 낮은 것부터 조회. 조회용
     *
     * @return sortOrder 기준으로 오름차순 정렬된 RunLevel 리스트
     */
    public List<RunLevel> findAllByOrderBySortOrderAsc() {
        Query query = em.createQuery("select rl from RunLevel rl order by rl.sortOrder", RunLevel.class);
        return query.getResultList();
    }

    /**
     * 유저 생성시 사용하려고 만듦
     *
     * @param order
     * @return
     */
    public Optional<RunLevel> findBySortOrder(Integer order) {
        Query query = em.createQuery("select rl from RunLevel rl where rl.sortOrder = :order", RunLevel.class);
        query.setParameter("order", order);
        try {
            return Optional.of((RunLevel) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

}
