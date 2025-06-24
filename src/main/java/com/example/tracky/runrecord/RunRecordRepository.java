package com.example.tracky.runrecord;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RunRecordRepository {

    private final EntityManager em;

    /**
     * RunRecord 엔티티 단일 반환
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
     * - pictures
     * 
     * @param id -> runRecordId
     * @return
     */
    public Optional<RunRecord> findByIdJoin(Integer id) {
        Query query = em.createQuery(
                "select r from RunRecord r left join fetch r.runSegments left join fetch r.pictures where r.id = :id",
                RunRecord.class);
        query.setParameter("id", id);

        try {
            return Optional.of((RunRecord) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }
}