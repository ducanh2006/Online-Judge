package com.onlinejudge.dto;

import java.time.LocalDateTime;

import com.onlinejudge.entity.SubmissionEntity.SubmissionStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionDTO {
    private int id;

    private int userId;     // map từ submission.user.id
    private int problemId;  // map từ submission.problem.id

    private String yourSolution;
    private Short score;
    private LocalDateTime submittedAt;
    private SubmissionStatus status;
}
