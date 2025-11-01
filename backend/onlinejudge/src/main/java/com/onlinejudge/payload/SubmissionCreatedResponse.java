package com.onlinejudge.payload;

import com.onlinejudge.entity.SubmissionEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubmissionCreatedResponse {
    private Integer submissionId;
    private SubmissionEntity.SubmissionStatus status;
 
}
