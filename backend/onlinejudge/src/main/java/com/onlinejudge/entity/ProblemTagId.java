package com.onlinejudge.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Cung cấp Getters/Setters
@Builder
@NoArgsConstructor // <--- QUAN TRỌNG: Cung cấp constructor mặc định không tham số
@AllArgsConstructor // Cung cấp constructor đầy đủ tham số
@Embeddable
public class ProblemTagId implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "problem_id")
    private Integer problemId;
    @Column(name = "tag_id")
    private Integer tagId;
}
