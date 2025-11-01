package com.onlinejudge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlinejudge.entity.UserEntity;
import com.onlinejudge.payload.JwtResponse;
import com.onlinejudge.payload.LoginRequest;
import com.onlinejudge.payload.RegisterRequest;
import com.onlinejudge.repository.UserRepository;
import com.onlinejudge.service.JwtUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtUtils jwtUtils;

    /**
     * API login: xác thực user và trả về JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Xác thực username/password
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Lấy user từ DB
        UserEntity user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.getRole();

        // Sinh JWT token (id, username, role)
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole().name());

        // Trả token cho client
        return ResponseEntity.ok(new JwtResponse(token));
    }

    /**
     * API register: đăng ký user mới
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username exists");
        }
        if (signUpRequest.getEmail() != null && userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email exists");
        }

        // Mã hoá password
        String encodedPassword = encoder.encode(signUpRequest.getPassword());

        // Tạo user mới
        UserEntity newUser = UserEntity.builder()
            .username(signUpRequest.getUsername())
            .password(encodedPassword)
            .email(signUpRequest.getEmail())
            .fullName(signUpRequest.getFullName())
            .displayName(signUpRequest.getDisplayName())
            .role(UserEntity.Role.USER)
            .build();

        userRepository.save(newUser);

        return ResponseEntity.status(201).body("Created");
    }
}
