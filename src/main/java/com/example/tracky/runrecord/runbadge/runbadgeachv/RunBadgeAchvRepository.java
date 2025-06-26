package com.example.tracky.runrecord.runbadge.runbadgeachv;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.user.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RunBadgeAchvRepository {

    private final EntityManager em;

    /**
     * 뱃지 획득 내역(RunBadgeAchv)을 저장합니다.
     *
     * @param runBadgeAchv 저장할 엔티티
     */
    public void save(RunBadgeAchv runBadgeAchv) {
        em.persist(runBadgeAchv);
    }

    /**
     * 뱃지 획득 내역(RunBadgeAchv)을 삭제합니다.
     *
     * @param runBadgeAchv 삭제할 엔티티
     */
    public void delete(RunBadgeAchv runBadgeAchv) {
        // 영속성 컨텍스트에 포함되어 있지 않은 엔티티일 수 있으므로, merge 후 remove 하는 것이 안전합니다.
        if (!em.contains(runBadgeAchv)) {
            runBadgeAchv = em.merge(runBadgeAchv);
        }
        em.remove(runBadgeAchv);
    }

    /**
     * 특정 달리기 기록과 뱃지의 조합으로 획득 내역이 존재하는지 확인합니다.
     *
     * @param runRecord 검사할 달리기 기록
     * @param runBadge  검사할 뱃지
     * @return 존재하면 true
     */
    public boolean existsByRunRecordAndRunBadge(RunRecord runRecord, RunBadge runBadge) {
        // COUNT 쿼리를 사용하면 전체 엔티티를 가져오지 않아 효율적입니다.
        Long count = em.createQuery(
                        "SELECT COUNT(rba) FROM RunBadgeAchv rba WHERE rba.runRecord = :runRecord AND rba.runBadge = :runBadge", Long.class)
                .setParameter("runRecord", runRecord)
                .setParameter("runBadge", runBadge)
                .getSingleResult();
        return count > 0;
    }

    /**
     * 특정 사용자가 특정 뱃지를 보유하고 있는지 확인합니다. (업적 뱃지 중복 방지용)
     *
     * @param runBadge 검사할 뱃지
     * @param user     검사할 사용자
     * @return 보유하고 있으면 true
     */
    public boolean existsByRunBadgeAndRunRecord_User(RunBadge runBadge, User user) {
        // RunBadgeAchv에서 RunRecord를 거쳐 User까지 경로를 따라 조건을 설정합니다.
        Long count = em.createQuery(
                        "SELECT COUNT(rba) FROM RunBadgeAchv rba WHERE rba.runBadge = :runBadge AND rba.runRecord.user = :user", Long.class)
                .setParameter("runBadge", runBadge)
                .setParameter("user", user)
                .getSingleResult();
        return count > 0;
    }

    /**
     * 특정 사용자가 보유한 특정 '기록' 뱃지의 획득 정보를 조회합니다. (기록 뱃지 이전/갱신용)
     *
     * @param runBadge 조회할 뱃지
     * @param user     조회할 사용자
     * @return 획득 정보가 담긴 Optional 객체
     */
    public Optional<RunBadgeAchv> findByRunBadgeAndRunRecord_User(RunBadge runBadge, User user) {
        List<RunBadgeAchv> results = em.createQuery(
                        "SELECT rba FROM RunBadgeAchv rba WHERE rba.runBadge = :runBadge AND rba.runRecord.user = :user", RunBadgeAchv.class)
                .setParameter("runBadge", runBadge)
                .setParameter("user", user)
                .setMaxResults(1) // 결과는 하나만 필요하므로 성능 최적화
                .getResultList();

        // getSingleResult()는 결과가 없으면 예외를 던지므로, getResultList()로 받은 후 Optional로 감싸는 것이 안전합니다.
        return results.stream().findFirst();
    }
}
