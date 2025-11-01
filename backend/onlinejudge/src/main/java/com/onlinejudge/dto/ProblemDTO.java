package com.onlinejudge.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProblemDTO {
    private int id;
    private String title;
    private byte difficulty;
    private LocalDateTime lastUpdated;
    private SubjectDTO subject;
    private List<TagDTO> tags;
    private String description;
    private String solution;
}
