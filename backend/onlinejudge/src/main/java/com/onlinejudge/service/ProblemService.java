package com.onlinejudge.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.onlinejudge.dto.PageResponse;
import com.onlinejudge.dto.ProblemDTO;
import com.onlinejudge.mapper.ProblemMapper;
import com.onlinejudge.entity.ProblemEntity;
import com.onlinejudge.entity.ProblemTagEntity;
import com.onlinejudge.entity.ProblemTagId;
import com.onlinejudge.entity.SubjectEntity;
import com.onlinejudge.entity.TagEntity;
import com.onlinejudge.payload.ProblemRequest;
import com.onlinejudge.repository.ProblemRepository;
import com.onlinejudge.repository.SubjectRepository;
import com.onlinejudge.repository.TagRepository;

import jakarta.transaction.Transactional;

@Service
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final SubjectRepository subjectRepository;
    private final TagRepository tagRepository;
    private final ProblemMapper problemMapper;

    public ProblemService(ProblemRepository problemRepository, SubjectRepository subjectRepository, TagRepository tagRepository, ProblemMapper problemMapper) {
        this.problemRepository = problemRepository;
        this.subjectRepository = subjectRepository;
        this.tagRepository = tagRepository;
        this.problemMapper = problemMapper;
    }

    public ProblemDTO getProblemDetails(int id, boolean isAdmin) {
        ProblemEntity problem = problemRepository.findProblemByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        return problemMapper.toDetailDto(problem, true);
    }

    public PageResponse<ProblemDTO> searchProblems(Integer problemId, List<Integer> subjectIds, List<Integer> tagIds,
                                                   boolean matchAll, int page, int size, String sort) {
        Sort sortOrder = Sort.by(Sort.Direction.DESC, "lastUpdated");
        if (sort != null) {
            try {
                String[] parts = sort.split(",");
                if (parts.length == 2) {
                    sortOrder = Sort.by(new Sort.Order(Sort.Direction.fromString(parts[1]), parts[0]));
                }
            } catch (Exception e) {}
        }
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<ProblemEntity> problemPage;

        if (problemId != null) {
            ProblemDTO problem = getProblemDetails(problemId, false);
            PageResponse<ProblemDTO> response = PageResponse.<ProblemDTO>builder()
                    .content(Collections.singletonList(problem))
                    .pageNumber(1)
                    .pageSize(1)
                    .totalElements(1)
                    .totalPages(1)
                    .isLast(true)
                    .build();
            return response;
        } else if (tagIds != null && !tagIds.isEmpty()) {
            problemPage = problemRepository.searchByTagIds(tagIds, matchAll, pageable);
        } else if (subjectIds != null && !subjectIds.isEmpty()) {
            problemPage = problemRepository.searchBySubjectIds(subjectIds, pageable);
        } else {
            problemPage = problemRepository.findAllProblemsWithDetails(pageable);
        }

        List<ProblemDTO> dtos = problemPage.getContent().stream()
                .map(problemMapper::toListDto)
                .collect(Collectors.toList());

        return PageResponse.<ProblemDTO>builder()
                .content(dtos)
                .pageNumber(problemPage.getNumber() + 1)
                .pageSize(problemPage.getSize())
                .totalElements(problemPage.getTotalElements())
                .totalPages(problemPage.getTotalPages())
                .isLast(problemPage.isLast())
                .build();
    }

    @Transactional
    public ProblemDTO createProblem(ProblemRequest request) {
        SubjectEntity subject = subjectRepository.findById(request.getSubjectId()).orElseThrow(() -> new RuntimeException("Subject not found"));
        ProblemEntity problem = ProblemEntity.builder().title(request.getTitle()).description(request.getDescription()).solution(request.getSolution()).difficulty(request.getDifficulty()).subject(subject).build();
        Set<TagEntity> tags = resolveTags(request.getTags());
        problem.setProblemTagEntities(syncTags(problem, tags));
        ProblemEntity savedProblem = problemRepository.save(problem);
        return problemMapper.toDetailDto(savedProblem, true);
    }

    @Transactional
    public ProblemDTO updateProblem(int id, ProblemRequest request) {
        ProblemEntity problem = problemRepository.findById(id).orElseThrow(() -> new RuntimeException("Problem not found"));
        SubjectEntity subject = subjectRepository.findById(request.getSubjectId()).orElseThrow(() -> new RuntimeException("Subject not found"));
        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setSolution(request.getSolution());
        problem.setDifficulty(request.getDifficulty());
        problem.setSubject(subject);
        Set<TagEntity> tags = resolveTags(request.getTags());
        problem.getProblemTagEntities().clear();
        problem.getProblemTagEntities().addAll(syncTags(problem, tags));
        ProblemEntity updatedProblem = problemRepository.save(problem);
        return problemMapper.toDetailDto(updatedProblem, true);
    }

    public void deleteProblem(int id) {
        if (!problemRepository.existsById(id)) throw new RuntimeException("Problem not found");
        problemRepository.deleteById(id);
    }

    private Set<TagEntity> resolveTags(List<String> tagIdentifiers) {
        if (CollectionUtils.isEmpty(tagIdentifiers)) return new HashSet<>();
        Set<Integer> tagIds = tagIdentifiers.stream().filter(t -> t.matches("\\d+")).map(Integer::parseInt).collect(Collectors.toSet());
        Set<String> tagNames = tagIdentifiers.stream().filter(t -> !t.matches("\\d+")).map(String::toLowerCase).collect(Collectors.toSet());
        Set<TagEntity> existingTags = new HashSet<>();
        if(!tagIds.isEmpty()) {
            Set<TagEntity> foundByIds = tagRepository.findByIdIn(tagIds);
            if(foundByIds.size() != tagIds.size()) throw new RuntimeException("One or more tags not found by ID.");
            existingTags.addAll(foundByIds);
        }
        if(!tagNames.isEmpty()) {
            List<TagEntity> foundByNames = tagRepository.findByNameIn(tagNames);
            existingTags.addAll(foundByNames);
            Set<String> foundNames = foundByNames.stream().map(TagEntity::getName).collect(Collectors.toSet());
            tagNames.stream().filter(name -> !foundNames.contains(name)).map(newName -> TagEntity.builder().name(newName).build()).forEach(newTag -> existingTags.add(tagRepository.save(newTag)));
        }
        return existingTags;
    }

    private Set<ProblemTagEntity> syncTags(ProblemEntity problem, Set<TagEntity> tags) {
        return tags.stream().map(tag -> ProblemTagEntity.builder().id(new ProblemTagId(problem.getId(), tag.getId())).problem(problem).tag(tag).build()).collect(Collectors.toSet());
    }
}
