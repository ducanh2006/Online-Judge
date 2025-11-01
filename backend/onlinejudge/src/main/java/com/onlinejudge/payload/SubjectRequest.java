package com.onlinejudge.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubjectRequest {
    @NotBlank @Size(max = 100) private String name;
}
