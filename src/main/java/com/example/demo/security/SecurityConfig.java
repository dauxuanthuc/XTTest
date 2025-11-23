package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;
import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
            // allow preflight requests
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // allow error endpoint
            .requestMatchers("/error").permitAll()
            // PUBLIC access
            .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
            // Ôn bài PUBLIC - practice endpoint không cần đăng nhập
            .requestMatchers("/api/practice/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/question-sets/**").permitAll()
            // Admin routes
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            // Teacher routes
            .requestMatchers("/api/teacher/**").hasRole("TEACHER")
            // User general routes
            .requestMatchers("/api/user/**").hasAnyRole("USER","TEACHER","ADMIN")
            // POST, PUT, DELETE on question sets require authentication
            .requestMatchers(HttpMethod.POST, "/api/question-sets/**").authenticated()
            .requestMatchers(HttpMethod.PUT, "/api/question-sets/**").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/question-sets/**").authenticated()
            // Remaining student routes require USER role (all POST/PUT/DELETE)
            .requestMatchers(HttpMethod.POST, "/api/student/**").hasRole("USER")
            .requestMatchers(HttpMethod.PUT, "/api/student/**").hasRole("USER")
            .requestMatchers(HttpMethod.DELETE, "/api/student/**").hasRole("USER")
            // Other GET requests on /api/student (except /question-sets which is already permitAll)
            .requestMatchers(HttpMethod.GET, "/api/student/exams/**").hasRole("USER")
            .requestMatchers(HttpMethod.GET, "/api/student/results/**").hasRole("USER")
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // allow H2 console frames
        http.headers().frameOptions().disable();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
