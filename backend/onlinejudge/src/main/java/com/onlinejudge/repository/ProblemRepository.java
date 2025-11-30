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

    // Phương thức này giữ nguyên vì nó là truy vấn chi tiết (fetch eager là hợp lý)
    @Query("SELECT p FROM ProblemEntity p " +
            "LEFT JOIN FETCH p.subject " +
            "LEFT JOIN FETCH p.problemTagEntities pt " +
            "LEFT JOIN FETCH pt.tag " +
            "WHERE p.id = :id")
    Optional<ProblemEntity> findProblemByIdWithDetails(@Param("id") Integer id);

    @Query(value = "SELECT p FROM ProblemEntity p " + // ĐÃ BỎ LEFT JOIN FETCH p.subject
            "WHERE " +
            "   ((:subjectIds IS NULL) OR (p.subject.id IN :subjectIds)) " +
            "AND " +
            "   ((:tagIds IS NULL) OR p.id IN (" +
            "       SELECT p2.id FROM ProblemEntity p2 " +
            "       JOIN p2.problemTagEntities pt2 " +
            "       JOIN pt2.tag t2 " +
            "       WHERE t2.id IN :tagIds " +
            "       GROUP BY p2.id " +
            "       HAVING COUNT(DISTINCT t2.id) = :tagCount" +
            "   ))",

            // countQuery giữ nguyên vì nó đã không có FETCH
            countQuery = "SELECT COUNT(DISTINCT p) FROM ProblemEntity p " +
                    "WHERE " +
                    "   ((:subjectIds IS NULL) OR (p.subject.id IN :subjectIds)) " +
                    "AND " +
                    "   ((:tagIds IS NULL) OR p.id IN (" +
                    "       SELECT p2.id FROM ProblemEntity p2 " +
                    "       JOIN p2.problemTagEntities pt2 " +
                    "       JOIN pt2.tag t2 " +
                    "       WHERE t2.id IN :tagIds " +
                    "       GROUP BY p2.id " +
                    "       HAVING COUNT(DISTINCT t2.id) = :tagCount" +
                    "   ))"
    )
    Page<ProblemEntity> searchProblems(
            @Param("subjectIds") List<Integer> subjectIds,
            @Param("tagIds") List<Integer> tagIds,
            @Param("tagCount") Long tagCount,
            Pageable pageable
    );
}