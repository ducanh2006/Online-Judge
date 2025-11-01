package com.onlinejudge.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.onlinejudge.dto.TagDTO;
import com.onlinejudge.entity.TagEntity;
import com.onlinejudge.repository.TagRepository;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) { this.tagRepository = tagRepository; }

    public List<TagDTO> getAllTags(String query) {
        List<TagEntity> tags = StringUtils.hasText(query) ? tagRepository.findByNameContainsIgnoreCase(query) : tagRepository.findAll();
        return tags.stream().map(t -> TagDTO.builder().id(t.getId()).name(t.getName()).build()).collect(Collectors.toList());
    }

    public TagDTO createTag(com.onlinejudge.payload.TagRequest request) {
        if(tagRepository.findByNameIgnoreCase(request.getName()).isPresent()) throw new RuntimeException("Tag exists");
        TagEntity tag = TagEntity.builder().name(request.getName()).build();
        TagEntity saved = tagRepository.save(tag);
        return TagDTO.builder().id(saved.getId()).name(saved.getName()).build();
    }

    public void deleteTag(int id) {
        if (!tagRepository.existsById(id)) throw new RuntimeException("Tag not found");
        tagRepository.deleteById(id);
    }
}
