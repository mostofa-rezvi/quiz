package com.encapsulearn.quiz_api.controller;

import com.encapsulearn.quiz_api.dto.AttemptResultDto;
import com.encapsulearn.quiz_api.dto.AttemptStartResponseDto;
import com.encapsulearn.quiz_api.dto.AttemptSubmissionRequest;
import com.encapsulearn.quiz_api.service.AttemptService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempts")
public class AttemptController {

    @Autowired
    private AttemptService attemptService;

    @PostMapping("/start/{quizId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<AttemptStartResponseDto> startAttempt(@PathVariable Long quizId) {
        try {
            AttemptStartResponseDto newAttempt = attemptService.startAttempt(quizId);
            return new ResponseEntity<>(newAttempt, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/submit/{attemptId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> submitAttempt(@PathVariable Long attemptId, @RequestBody AttemptSubmissionRequest submissionRequest) {
        try {
            AttemptResultDto result = attemptService.submitAttempt(attemptId, submissionRequest);
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            String jsonResult = mapper.writeValueAsString(result);
            return new ResponseEntity<>(jsonResult, HttpStatus.OK);
        } catch (RuntimeException | JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<AttemptResultDto>> getAllAttemptHistory() {
        List<AttemptResultDto> history = attemptService.getAllAttemptHistory();
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    @GetMapping("/{attemptId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<AttemptResultDto> getAttemptDetails(@PathVariable Long attemptId) {
        try {
            AttemptResultDto details = attemptService.getAttemptDetails(attemptId);
            return new ResponseEntity<>(details, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}