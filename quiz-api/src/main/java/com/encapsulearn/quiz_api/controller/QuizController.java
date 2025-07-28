package com.encapsulearn.quiz_api.controller;

import com.encapsulearn.quiz_api.dto.OptionDto;
import com.encapsulearn.quiz_api.dto.QuestionDto;
import com.encapsulearn.quiz_api.dto.QuizCreateRequest;
import com.encapsulearn.quiz_api.dto.QuizResponseDto;
import com.encapsulearn.quiz_api.entity.Quiz;
import com.encapsulearn.quiz_api.enums.QuestionType;
import com.encapsulearn.quiz_api.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/manual")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<QuizResponseDto> createQuizManually(@RequestBody QuizCreateRequest request) {
        QuizResponseDto createdQuiz = quizService.createQuizManually(request);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<QuizResponseDto>> getAllQuizzes() {
        List<QuizResponseDto> quizzes = quizService.getAllQuizzes();
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizCreateRequest> getQuizById(@PathVariable Long id) {
        try {
            Quiz quiz = quizService.getQuizById(id);

            QuizCreateRequest quizDto = new QuizCreateRequest();
            quizDto.setTitle(quiz.getTitle());
            quizDto.setDurationMinutes(quiz.getDurationMinutes());
            quizDto.setType(quiz.getType());

            List<QuestionDto> questionDtos = quiz.getQuestions().stream().map(q -> {
                QuestionDto qDto = new QuestionDto();
                qDto.setId(q.getId());
                qDto.setText(q.getText());
                qDto.setType(q.getType());
                if (q.getType() == QuestionType.MCQ) {
                    qDto.setOptions(q.getOptions().stream().map(o -> new OptionDto(o.getText(), o.isCorrect())).collect(Collectors.toList()));
                } else if (q.getType() == QuestionType.SHORT_ANSWER) {
                    qDto.setCorrectAnswer(q.getCorrectAnswer());
                }
                return qDto;
            }).collect(Collectors.toList());
            quizDto.setQuestions(questionDtos);

            return new ResponseEntity<>(quizDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<QuizResponseDto> updateQuiz(@PathVariable Long id, @RequestBody QuizCreateRequest request) {
        try {
            QuizResponseDto updatedQuiz = quizService.updateQuiz(id, request);
            return new ResponseEntity<>(updatedQuiz, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        try {
            quizService.deleteQuiz(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
