package com.encapsulearn.quiz_api.controller;

import com.encapsulearn.quiz_api.dto.PasswordChangeRequest;
import com.encapsulearn.quiz_api.dto.UserUpdateRequest;
import com.encapsulearn.quiz_api.entity.User;
import com.encapsulearn.quiz_api.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @PutMapping("/update-username")
    public ResponseEntity<String> updateUsername(@AuthenticationPrincipal User currentUser, @RequestBody UserUpdateRequest request) {
        try {
            userManagementService.updateUsername(currentUser.getId(), request.getNewUsername());
            return ResponseEntity.ok("Username updated successfully. Please log in again.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal User currentUser, @RequestBody PasswordChangeRequest request) {
        try {
            userManagementService.changePassword(currentUser.getId(), request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully. Please log in again.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
