package com.encapsulearn.quiz_api.controller;

import com.encapsulearn.quiz_api.dto.AuthResponse;
import com.encapsulearn.quiz_api.dto.LoginRequest;
import com.encapsulearn.quiz_api.dto.RegisterRequest;
import com.encapsulearn.quiz_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
