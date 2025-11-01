package com.onlinejudge.spec;

import org.springframework.data.jpa.domain.Specification;

import com.onlinejudge.entity.SubmissionEntity;

import jakarta.persistence.criteria.Predicate;

public class SubmissionSpecification {
     public static Specification<SubmissionEntity> withCriteria(Integer userId, Integer problemId) {
         return (root, query, cb) -> {
             Predicate predicate = cb.conjunction();
             if (userId != null) {
                 predicate = cb.and(predicate, cb.equal(root.get("user").get("id"), userId));
             }
             if (problemId != null) {
                 predicate = cb.and(predicate, cb.equal(root.get("problem").get("id"), problemId));
             }
             return predicate;
         };
     }
}
