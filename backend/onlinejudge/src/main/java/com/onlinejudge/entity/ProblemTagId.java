package com.onlinejudge.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProblemTagId implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "problem_id")
    private Integer problemId;
    @Column(name = "tag_id")
    private Integer tagId;

    public ProblemTagId() {}

    public ProblemTagId(Integer problemId, Integer tagId) {
        this.problemId = problemId;
        this.tagId = tagId;
    }

    public Integer getProblemId() { return problemId; }
    public void setProblemId(Integer problemId) { this.problemId = problemId; }
    public Integer getTagId() { return tagId; }
    public void setTagId(Integer tagId) { this.tagId = tagId; }
}
