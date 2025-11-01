package com.onlinejudge.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
class RegisterRequest {
    @NotBlank @Size(min = 3, max = 50) private String username;
    @NotBlank @Size(min = 6, max = 40) private String password;
    @Size(max = 100) @Email private String email;
    private String fullName;
    private String displayName;
}

@Data
class LoginRequest {
    @NotBlank private String username;
    @NotBlank private String password;
}

@Data
class JwtResponse {
    private String accessToken;
    public JwtResponse(String accessToken) { this.accessToken = accessToken; }
}

@Data
class ProblemRequest {
    @NotBlank @Size(max = 255) private String title;
    @NotBlank private String description;
    private String solution;
    @NotNull @Min(1) @Max(10) private Byte difficulty;
    @NotNull private Integer subjectId;
    private List<String> tags;
}

@Data
class TagRequest {
    @NotBlank @Size(max = 100) private String name;
}

@Data
class SubjectRequest {
    @NotBlank @Size(max = 100) private String name;
}

@Data
class CreateSubmissionRequest {
    @NotNull private Integer problemId;
    @NotBlank private String yourSolution;
}

