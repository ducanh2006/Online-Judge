package com.onlinejudge.payload;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProblemRequest {
    @NotBlank @Size(max = 255) private String title;
    @NotBlank private String description;
    private String solution;
    @NotNull @Min(1) @Max(10) private Byte difficulty;
    @NotNull private Integer subjectId;
    private List<String> tags;
}
