package com.example.tracky.runrecord;

import java.time.LocalDateTime;
import java.util.List;
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
     * RunRecord 엔티티 전체 조회
     *
     * @return
     */
    public List<RunRecord> findAllByUserIdJoin() {
        // TODO : join fetch 추가해서 모든 연관 엔티티 다 가져오기
        Query query = em.createQuery("select r from RunRecord r ", RunRecord.class);
        List<RunRecord> runRecords = query.getResultList();
        return runRecords;
    }

    /**
     * 특정 기간 동안 생성된 RunRecord 엔티티를 조회
     * <p></p>
     * - createdAt 기준으로 시작일~종료일 사이의 기록만 필터링
     *
     * @param start 시작일시
     * @param end   종료일시
     * @return 기간 내 러닝 기록 리스트
     */
    public List<RunRecord> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        Query query = em.createQuery(
                "SELECT r FROM RunRecord r WHERE r.createdAt BETWEEN :start AND :end",
                RunRecord.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        List<RunRecord> runRecords = query.getResultList();
        return runRecords;
    }

}