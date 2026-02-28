package com.onlinejudge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onlinejudge.dto.PageResponse;
import com.onlinejudge.dto.ProblemDTO;
import com.onlinejudge.payload.request.ProblemRequest;
import com.onlinejudge.service.ProblemService;

@RestController
@CrossOrigin
@RequestMapping("/api/problems")
public class ProblemController {
    @Autowired
    private ProblemService problemService;

    @GetMapping
    public ResponseEntity<PageResponse<ProblemDTO>> searchProblems(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) List<Integer> subject,
            @RequestParam(required = false) List<Integer> tag,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastUpdated,desc") String sort) {

        page = Math.max(1, page);
        size = Math.min(100, Math.max(1, size));
        System.out.println(" page = "+page+" size = "+size);
        PageResponse<ProblemDTO> response = problemService.searchProblems( id, subject, tag, page - 1, size, sort);
        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<ProblemDTO> createProblem(@RequestBody ProblemRequest request) {
        ProblemDTO createdProblem = problemService.createProblem(request);
        return ResponseEntity.status(201).body(createdProblem);
    }
}
