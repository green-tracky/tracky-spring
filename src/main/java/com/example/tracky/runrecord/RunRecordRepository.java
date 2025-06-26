package com.example.tracky.runrecord;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RunRecordRepository {

    private final EntityManager em;

    /**
     * 테스트용 findById
     * 삭제용 findById
     *
     * @param id -> runRecordId
     * @return
     */
    public Optional<RunRecord> findById(Integer id) {
        Query query = em.createQuery("select r from RunRecord r where r.id = :id", RunRecord.class);
        query.setParameter("id", id);

        try {
            return Optional.of((RunRecord) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

    /**
     * RunRecord 엔티티 퍼시스트 컨텍스트에 저장
     *
     * @param runRecord
     * @return RunRecord
     */
    public RunRecord save(RunRecord runRecord) {
        em.persist(runRecord);
        return runRecord;
    }

    /**
     * join fetch
     * <p>
     * - runSegments
     * <p>
     * 좌표를 가져오지 않는 이유: 좌표가 너무 많으면 카테시안 곱 문제가 발생하여 너무 많은 row를 만들게 된다. 따라서 구간까지만 조회하고 배치로 좌표값을 가져오자
     * <p>
     * distinct 를 사용하는 이유: 메모리에서 러닝 객체를 더 만들지 마라
     *
     * @param id -> runRecordId
     * @return
     */
    public Optional<RunRecord> findByIdJoin(Integer id) {
        Query query = em.createQuery("select distinct r from RunRecord r join fetch r.runSegments where r.id = :id", RunRecord.class);
        query.setParameter("id", id);

        try {
            return Optional.of((RunRecord) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

    /**
     * 러닝 기록 삭제
     *
     * @param runRecord
     */
    public void delete(RunRecord runRecord) {
        em.remove(runRecord);
    }
}