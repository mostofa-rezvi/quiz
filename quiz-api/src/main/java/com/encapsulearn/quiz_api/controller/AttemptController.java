package com.encapsulearn.quiz_api.controller;

import com.encapsulearn.quiz_api.dto.AttemptResultDto;
import com.encapsulearn.quiz_api.dto.AttemptSubmissionRequest;
import com.encapsulearn.quiz_api.entity.Attempt;
import com.encapsulearn.quiz_api.service.AttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempts")
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from Angular
public class AttemptController {

    @Autowired
    private AttemptService attemptService;

    // Start a new quiz attempt
    @PostMapping("/start/{quizId}")
    public ResponseEntity<Attempt> startAttempt(@PathVariable Long quizId) {
        try {
            Attempt newAttempt = attemptService.startAttempt(quizId);
            return new ResponseEntity<>(newAttempt, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Quiz not found
        }
    }

    // Submit quiz answers
    @PostMapping("/submit/{attemptId}")
    public ResponseEntity<AttemptResultDto> submitAttempt(@PathVariable Long attemptId, @RequestBody AttemptSubmissionRequest submissionRequest) {
        try {
            AttemptResultDto result = attemptService.submitAttempt(attemptId, submissionRequest);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // e.g., attempt not found, or already submitted
        }
    }

    // Get all attempt history
    @GetMapping("/history")
    public ResponseEntity<List<AttemptResultDto>> getAllAttemptHistory() {
        List<AttemptResultDto> history = attemptService.getAllAttemptHistory();
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    // Get detailed result of a specific attempt
    @GetMapping("/{attemptId}")
    public ResponseEntity<AttemptResultDto> getAttemptDetails(@PathVariable Long attemptId) {
        try {
            AttemptResultDto details = attemptService.getAttemptDetails(attemptId);
            return new ResponseEntity<>(details, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
