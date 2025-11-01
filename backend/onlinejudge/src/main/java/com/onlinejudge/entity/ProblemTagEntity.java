package com.onlinejudge.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem_tag")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemTagEntity {
    @EmbeddedId
    private ProblemTagId id;

    @MapsId("problemId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private ProblemEntity problem;

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private TagEntity tag;
}
