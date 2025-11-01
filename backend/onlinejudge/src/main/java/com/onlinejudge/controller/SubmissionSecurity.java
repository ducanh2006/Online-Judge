package com.onlinejudge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.onlinejudge.repository.SubmissionRepository;

@Component("submissionSecurity")
public class SubmissionSecurity {
    @Autowired SubmissionRepository submissionRepository;
    public boolean isOwner(Authentication authentication, int submissionId) {
        // simplified
        return submissionRepository.findById(submissionId).map(s -> s.getUser().getId() == 0).orElse(false);
    }
}
