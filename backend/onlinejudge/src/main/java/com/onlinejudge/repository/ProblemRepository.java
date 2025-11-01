package com.onlinejudge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.onlinejudge.entity.ProblemEntity;

@Repository
public interface ProblemRepository extends JpaRepository<ProblemEntity, Integer> {

    @Query("SELECT p FROM ProblemEntity p " +
           "LEFT JOIN FETCH p.subject " +
           "LEFT JOIN FETCH p.problemTagEntities pt " +
           "LEFT JOIN FETCH pt.tag " +
           "WHERE p.id = :id")
    Optional<ProblemEntity> findProblemByIdWithDetails(@Param("id") Integer id);

    @Query("SELECT DISTINCT p FROM ProblemEntity p " +
           "LEFT JOIN FETCH p.subject " +
           "LEFT JOIN FETCH p.problemTagEntities pt " +
           "LEFT JOIN FETCH pt.tag")
    Page<ProblemEntity> findAllProblemsWithDetails(Pageable pageable);

    @Query("SELECT DISTINCT p FROM ProblemEntity p " +
           "LEFT JOIN FETCH p.subject " +
           "LEFT JOIN FETCH p.problemTagEntities pt " +
           "LEFT JOIN FETCH pt.tag " +
           "WHERE p.subject.id IN :subjectIds")
    Page<ProblemEntity> searchBySubjectIds(@Param("subjectIds") List<Integer> subjectIds, Pageable pageable);

    @Query("SELECT DISTINCT p FROM ProblemEntity p " +
           "LEFT JOIN FETCH p.subject " +
           "LEFT JOIN FETCH p.problemTagEntities pt " +
           "LEFT JOIN FETCH pt.tag " +
           "WHERE pt.tag.id IN :tagIds")
    Page<ProblemEntity> searchByTagIdsOr(@Param("tagIds") List<Integer> tagIds, Pageable pageable);

    @Query("SELECT DISTINCT p FROM ProblemEntity p " +
           "LEFT JOIN FETCH p.subject " +
           "LEFT JOIN FETCH p.problemTagEntities pt " +
           "LEFT JOIN FETCH pt.tag " +
           "WHERE p.id IN (" +
           "    SELECT p2.id FROM ProblemEntity p2 " +
           "    JOIN p2.problemTagEntities pt2 " +
           "    JOIN pt2.tag t2 " +
           "    WHERE t2.id IN :tagIds " +
           "    GROUP BY p2.id " +
           "    HAVING COUNT(DISTINCT t2.id) = :tagCount" +
           ")")
    Page<ProblemEntity> searchByTagIdsAnd(@Param("tagIds") List<Integer> tagIds, @Param("tagCount") long tagCount, Pageable pageable);

    default Page<ProblemEntity> searchByTagIds(List<Integer> tagIds, boolean matchAll, Pageable pageable) {
        if (tagIds == null || tagIds.isEmpty()) {
            return findAllProblemsWithDetails(pageable);
        }
        if (matchAll) {
            return searchByTagIdsAnd(tagIds, tagIds.size(), pageable);
        } else {
            return searchByTagIdsOr(tagIds, pageable);
        }
    }
}
