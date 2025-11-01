package com.onlinejudge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.onlinejudge.entity.SubjectEntity;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Integer> {
    boolean existsByName(String name);
    @Query("SELECT COUNT(p) > 0 FROM ProblemEntity p WHERE p.subject.id = :subjectId")
    boolean isSubjectInUse(Integer subjectId);
}
