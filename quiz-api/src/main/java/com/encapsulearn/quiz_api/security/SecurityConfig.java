package com.encapsulearn.quiz_api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.encapsulearn.quiz_api.enums.Role.ROLE_ADMIN;
import static com.encapsulearn.quiz_api.enums.Role.ROLE_USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Allow OPTIONS requests for CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // <-- ADD THIS LINE

                        .requestMatchers("/api/auth/**").permitAll()

                        // Quizzes:
                        .requestMatchers(HttpMethod.GET, "/api/quizzes").hasAnyAuthority(ROLE_USER.name(), ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/quizzes/{id}").hasAnyAuthority(ROLE_USER.name(), ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/quizzes/upload", "/api/quizzes/manual").hasAuthority(ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/quizzes/**").hasAuthority(ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/quizzes/**").hasAuthority(ROLE_ADMIN.name())

                        // Attempts:
                        .requestMatchers(HttpMethod.POST, "/api/attempts/start/**", "/api/attempts/submit/**").hasAuthority(ROLE_USER.name())
                        .requestMatchers(HttpMethod.GET, "/api/attempts/history", "/api/attempts/**").hasAnyAuthority(ROLE_USER.name(), ROLE_ADMIN.name())

                        // User Management endpoints require authentication
                        .requestMatchers("/api/users/**").authenticated()

                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}