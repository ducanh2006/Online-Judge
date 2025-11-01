package com.onlinejudge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.onlinejudge.repository.UserRepository;


// @Component 
// class  UserDetailsEntity{
//     @Autowired 
//     UserRepository userRepository;

//     public Optional<UserEntity> findByUsername( String username){
//         return  userRepository.findByUsername(username);
//     }


//     public UserDetailsService userDetailsService( String username) {
//         return userRepository.findByUsername(username)
//                 .map(user -> org.springframework.security.core.userdetails.User.builder()
//                     .username(user.getUsername())
//                     .password(user.getPassword())
//                     .roles(user.getRole().name())
//                     .build())
//                 .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//     }
// }


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // mặc định mở hết
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
//     http
//         .csrf(csrf -> csrf.disable())
//         .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 👈 bật CORS
//         .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//         .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//     return http.build();
// }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // @Bean
    // public UserDetailsService UserDetailsEntity(UserRepository userRepository) {
    //     return username -> userRepository.findByUsername(username);
    // }
}
