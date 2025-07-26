package com.encapsulearn.quiz_api.controller;

import com.encapsulearn.quiz_api.dto.QuizCreateRequest;
import com.encapsulearn.quiz_api.dto.QuizResponseDto;
import com.encapsulearn.quiz_api.entity.Quiz;
import com.encapsulearn.quiz_api.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from Angular
public class QuizController {

    @Autowired
    private QuizService quizService;

    // Create Quiz Manually
    @PostMapping("/manual")
    public ResponseEntity<QuizResponseDto> createQuizManually(@RequestBody QuizCreateRequest request) {
        QuizResponseDto createdQuiz = quizService.createQuizManually(request);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    // Upload Excel File for Quiz Creation
    @PostMapping("/upload")
    public ResponseEntity<List<QuizResponseDto>> uploadQuizExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            List<QuizResponseDto> createdQuizzes = quizService.createQuizzesFromExcel(file);
            return new ResponseEntity<>(createdQuizzes, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // For Excel format errors
        }
    }

    // Get All Quizzes
    @GetMapping
    public ResponseEntity<List<QuizResponseDto>> getAllQuizzes() {
        List<QuizResponseDto> quizzes = quizService.getAllQuizzes();
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    // Get Quiz by ID (e.g., for attempting or updating)
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        try {
            Quiz quiz = quizService.getQuizById(id);
            return new ResponseEntity<>(quiz, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Update Quiz
    @PutMapping("/{id}")
    public ResponseEntity<QuizResponseDto> updateQuiz(@PathVariable Long id, @RequestBody QuizCreateRequest request) {
        try {
            QuizResponseDto updatedQuiz = quizService.updateQuiz(id, request);
            return new ResponseEntity<>(updatedQuiz, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete Quiz
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        try {
            quizService.deleteQuiz(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
