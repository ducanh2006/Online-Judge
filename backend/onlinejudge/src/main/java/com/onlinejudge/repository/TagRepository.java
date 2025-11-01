package com.onlinejudge.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.onlinejudge.entity.TagEntity;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Integer> {
    List<TagEntity> findByNameContainsIgnoreCase(String name);
    Optional<TagEntity> findByNameIgnoreCase(String name);
    List<TagEntity> findByNameIn(Collection<String> names);
    @Query("SELECT t FROM TagEntity t WHERE t.id IN :ids")
    Set<TagEntity> findByIdIn(Set<Integer> ids);
}
