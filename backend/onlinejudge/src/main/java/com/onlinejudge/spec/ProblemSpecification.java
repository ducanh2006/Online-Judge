package com.onlinejudge.spec;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import com.onlinejudge.entity.ProblemEntity;
import com.onlinejudge.entity.ProblemTagEntity;
import com.onlinejudge.entity.TagEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ProblemSpecification {

    public static Specification<ProblemEntity> withCriteria(
            List<Integer> subjectIds, List<Integer> tagIds, boolean matchAllTags) {
        return (root, query, cb) -> {
            query.distinct(true);
            Predicate predicate = cb.conjunction();

            if (!CollectionUtils.isEmpty(subjectIds)) {
                predicate = cb.and(predicate, root.get("subject").get("id").in(subjectIds));
            }

            if (!CollectionUtils.isEmpty(tagIds)) {
                Predicate tagPredicate = buildTagPredicate(root, query, cb, tagIds, matchAllTags);
                if(tagPredicate != null) {
                    predicate = cb.and(predicate, tagPredicate);
                }
            }

            return predicate;
        };
    }

    private static Predicate buildTagPredicate(Root<ProblemEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb, List<Integer> tagIds, boolean matchAllTags) {
        Join<ProblemEntity, ProblemTagEntity> problemTagJoin = root.join("problemTagEntities", JoinType.LEFT);
        Join<ProblemTagEntity, TagEntity> tagJoin = problemTagJoin.join("tag", JoinType.LEFT);
        
        Predicate tagPredicate = tagJoin.get("id").in(tagIds);
        
        if (matchAllTags) {
            query.where(tagPredicate)
                .groupBy(root.get("id"))
                .having(cb.equal(cb.count(root.get("id")), (long) tagIds.size()));
            return null;
        } else {
            return tagPredicate;
        }
    }
}
