package com.onlinejudge.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.onlinejudge.dto.ProblemDTO;
import com.onlinejudge.dto.SubjectDTO;
import com.onlinejudge.dto.TagDTO;
import com.onlinejudge.entity.ProblemEntity;

@Component
public class ProblemMapper {
    public ProblemDTO toListDto(ProblemEntity entity) {
        return ProblemDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .difficulty(entity.getDifficulty())
                .lastUpdated(entity.getLastUpdated())
                .subject(SubjectDTO.builder().id(entity.getSubject().getId()).name(entity.getSubject().getName()).build())
                .tags(entity.getProblemTagEntities().stream()
                        .map(pt -> TagDTO.builder().id(pt.getTag().getId()).name(pt.getTag().getName()).build())
                        .collect(Collectors.toList()))
                .build();
    }

    public ProblemDTO toDetailDto(ProblemEntity entity, boolean includeSolution) {
        ProblemDTO dto = toListDto(entity);
        dto.setDescription(entity.getDescription());
        if(includeSolution) dto.setSolution(entity.getSolution());
        return dto;
    }
}
