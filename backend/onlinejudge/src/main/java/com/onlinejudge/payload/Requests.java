// package com.onlinejudge.payload;

// import com.onlinejudge.entity.SubmissionEntity;
// import jakarta.validation.constraints.*;
// import lombok.AllArgsConstructor;
// import lombok.Data;

// import java.util.List;

// @Data
// public class RegisterRequest {
//     @NotBlank @Size(min = 3, max = 50) private String username;
//     @NotBlank @Size(min = 6, max = 40) private String password;
//     @Size(max = 100) @Email private String email;
//     private String fullName;
//     private String displayName;
// }

// @Data
// public class LoginRequest {
//     @NotBlank private String username;
//     @NotBlank private String password;
// }

// @Data
// public class JwtResponse {
//     private String accessToken;
//     public JwtResponse(String accessToken) { this.accessToken = accessToken; }
// }

// @Data
// public class ProblemRequest {
//     @NotBlank @Size(max = 255) private String title;
//     @NotBlank private String description;
//     private String solution;
//     @NotNull @Min(1) @Max(10) private Byte difficulty;
//     @NotNull private Integer subjectId;
//     private List<String> tags;
// }

// @Data
// public class TagRequest {
//     @NotBlank @Size(max = 100) private String name;
// }

// @Data
// public class SubjectRequest {
//     @NotBlank @Size(max = 100) private String name;
// }

// @Data
// public class CreateSubmissionRequest {
//     @NotNull private Integer problemId;
//     @NotBlank private String yourSolution;
// }

// @Data
// @AllArgsConstructor
// public class SubmissionCreatedResponse {
//     private Integer submissionId;
//     private SubmissionEntity.SubmissionStatus status;
// }
