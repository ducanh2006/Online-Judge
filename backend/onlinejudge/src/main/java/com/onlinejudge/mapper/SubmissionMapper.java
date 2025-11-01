package com.onlinejudge.mapper;

import org.springframework.stereotype.Component;

import com.onlinejudge.dto.SubmissionDTO;
import com.onlinejudge.entity.SubmissionEntity;

@Component
public class SubmissionMapper {

    public SubmissionDTO toDto(SubmissionEntity entity) {
        if (entity == null) {
            return null;
        }

        return SubmissionDTO.builder()
                .id(entity.getId())
                .problemId(entity.getProblem().getId())
                .userId(entity.getUser().getId())
                .yourSolution(entity.getYourSolution())
                .score(entity.getScore())
                .submittedAt(entity.getSubmittedAt())
                .status(entity.getStatus())
                .build();
    }
}
