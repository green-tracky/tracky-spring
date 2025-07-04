package com.example.tracky.runrecord.runsegments.runcoordinates;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RunCoordinateRepository {

    private final EntityManager em;

    /**
     * 삭제 확인용
     *
     * @param id runCoordinateId
     * @return
     */
    public Optional<RunCoordinate> findById(Integer id) {
        Query query = em.createQuery("select c from RunCoordinate c where c.id = :id");
        query.setParameter("id", id);

        try {
            return Optional.of((RunCoordinate) query.getSingleResult());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }

}