package com.onlinejudge.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private int id;
    private String username;
    private String email;
    private String fullName;
    private String displayName;
    private String avatarUrl;
    private String role;
    private LocalDateTime createdAt;
}
