package com.onlinejudge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onlinejudge.dto.PageResponse;
import com.onlinejudge.dto.ProblemDTO;
import com.onlinejudge.payload.ProblemRequest;
import com.onlinejudge.service.ProblemService;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {
    @Autowired
    private ProblemService problemService;

    @GetMapping
    public ResponseEntity<PageResponse<ProblemDTO>> searchProblems(
            @RequestParam(required = false) Integer problemId,
            @RequestParam(required = false) List<Integer> subject,
            @RequestParam(required = false) List<Integer> tag,
            @RequestParam(defaultValue = "false") boolean match,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastUpdated,desc") String sort) {

        page = Math.max(1, page);
        size = Math.min(100, Math.max(1, size));
        PageResponse<ProblemDTO> response = problemService.searchProblems(problemId, subject, tag, match, page - 1, size, sort);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemDTO> getProblemById(@PathVariable int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch("ROLE_ADMIN"::equals);
        ProblemDTO problem = problemService.getProblemDetails(id, isAdmin);
        return ResponseEntity.ok(problem);
    }

    @PostMapping
    public ResponseEntity<ProblemDTO> createProblem(@RequestBody ProblemRequest request) {
        ProblemDTO createdProblem = problemService.createProblem(request);
        return ResponseEntity.status(201).body(createdProblem);
    }
}
