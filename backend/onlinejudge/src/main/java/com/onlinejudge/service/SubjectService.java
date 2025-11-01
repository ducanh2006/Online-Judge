package com.onlinejudge.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.onlinejudge.dto.SubjectDTO;
import com.onlinejudge.entity.SubjectEntity;
import com.onlinejudge.repository.SubjectRepository;

@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) { this.subjectRepository = subjectRepository; }

    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll().stream().map(s -> SubjectDTO.builder().id(s.getId()).name(s.getName()).build()).collect(Collectors.toList());
    }

    public SubjectDTO createSubject(com.onlinejudge.payload.SubjectRequest request) {
        if(subjectRepository.existsByName(request.getName())) throw new RuntimeException("Subject exists");
        SubjectEntity subject = SubjectEntity.builder().name(request.getName()).build();
        SubjectEntity saved = subjectRepository.save(subject);
        return SubjectDTO.builder().id(saved.getId()).name(saved.getName()).build();
    }

    public void deleteSubject(int id) {
        if(subjectRepository.isSubjectInUse(id)) throw new RuntimeException("Subject in use");
        if (!subjectRepository.existsById(id)) throw new RuntimeException("Subject not found");
        subjectRepository.deleteById(id);
    }
}
