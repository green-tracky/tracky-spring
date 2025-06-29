package com.example.tracky.user.runlevel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RunLevelRepository {

    private final EntityManager em;

    public List<RunLevel> findAll() {
        return em.createQuery("select r from RunLevel r", RunLevel.class)
                .getResultList();
    }

    /**
     * 모든 레벨 정보를 'sortOrder' 기준으로 내림차순 정렬하여 조회합니다.
     * 레벨업 조건을 확인할 때, 가장 높은 레벨부터 검사하여 효율적으로 사용자의 현재 레벨을 찾기 위함입니다.
     *
     * @return sortOrder 기준으로 내림차순 정렬된 RunLevel 리스트
     */
    public List<RunLevel> findAllByOrderBySortOrderDesc() {
        Query query = em.createQuery("select rl from RunLevel rl order by rl.sortOrder", RunLevel.class);
        return query.getResultList();
    }

}
