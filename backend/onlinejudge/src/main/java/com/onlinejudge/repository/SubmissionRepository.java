package com.onlinejudge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.onlinejudge.entity.SubmissionEntity;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, Integer>, JpaSpecificationExecutor<SubmissionEntity> {}
