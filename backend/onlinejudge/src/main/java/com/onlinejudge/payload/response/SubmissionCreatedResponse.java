package com.onlinejudge.payload.response;

import com.onlinejudge.entity.SubmissionEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubmissionCreatedResponse {
    private Integer submissionId;
    private SubmissionEntity.SubmissionStatus status;
 
}
