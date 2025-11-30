package com.onlinejudge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlinejudge.dto.SubjectDTO;
import com.onlinejudge.payload.request.SubjectRequest;
import com.onlinejudge.service.SubjectService;


@CrossOrigin
@RestController
@RequestMapping("/api/subjects")
public class SubjectController {
    @Autowired private SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    
    @PostMapping
    public ResponseEntity<SubjectDTO> createSubject(@RequestBody SubjectRequest request, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build(); 
        }
        if (authentication.getAuthorities().isEmpty()) {
            return ResponseEntity.status(403).build(); 
        }
        

        // Kiểm tra có quyền ADMIN không
        boolean hasAdminRole = authentication.getAuthorities().stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        if (hasAdminRole) {
            return ResponseEntity.status(201).body(subjectService.createSubject(request));
        } else {
            return ResponseEntity.status(403).build(); 
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable int id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build(); 
        }
        if (authentication.getAuthorities().isEmpty()) {
            return ResponseEntity.status(403).build(); 
        }
        
        // Kiểm tra có quyền ADMIN không
        boolean hasAdminRole = authentication.getAuthorities().stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        
        if (hasAdminRole) {
            try {
                subjectService.deleteSubject(id);
                return ResponseEntity.ok().build();
            } catch (RuntimeException e) {
                if (e.getMessage().contains("not found")) {
                    return ResponseEntity.notFound().build();
                } else if (e.getMessage().contains("in use")) {
                    return ResponseEntity.status(409).build(); // Conflict - subject đang được sử dụng
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }
        } else {
            return ResponseEntity.status(403).build(); 
        }
    }
}
