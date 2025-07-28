package com.encapsulearn.quiz_api.service;

import com.encapsulearn.quiz_api.dto.AttemptAnswerDetailDto;
import com.encapsulearn.quiz_api.dto.AttemptResultDto;
import com.encapsulearn.quiz_api.dto.AttemptStartResponseDto;
import com.encapsulearn.quiz_api.dto.AttemptSubmissionRequest;
import com.encapsulearn.quiz_api.entity.*;
import com.encapsulearn.quiz_api.enums.QuestionType;
import com.encapsulearn.quiz_api.enums.Role;
import com.encapsulearn.quiz_api.repository.AttemptAnswerRepository;
import com.encapsulearn.quiz_api.repository.AttemptRepository;
import com.encapsulearn.quiz_api.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AttemptService {

    @Autowired
    private AttemptRepository attemptRepository;
    @Autowired
    private QuizService quizService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AttemptAnswerRepository attemptAnswerRepository;

    private User getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        throw new RuntimeException("User not authenticated or principal not found.");
    }

    @Transactional
    public AttemptStartResponseDto startAttempt(Long quizId) {
        User currentUser = getCurrentAuthenticatedUser();
        Quiz quiz = quizService.getQuizById(quizId);
        Attempt attempt = new Attempt();
        attempt.setQuiz(quiz);
        attempt.setUser(currentUser);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setScore(0);
        Attempt savedAttempt = attemptRepository.save(attempt);
        return new AttemptStartResponseDto(savedAttempt.getId());
    }

    @Transactional
    public AttemptResultDto submitAttempt(Long attemptId, AttemptSubmissionRequest submissionRequest) {
        User currentUser = getCurrentAuthenticatedUser();
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found with id: " + attemptId));

        if (!attempt.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: You are not authorized to submit this attempt.");
        }

        if (attempt.getEndTime() != null) {
            throw new RuntimeException("Attempt already submitted.");
        }

        attempt.setEndTime(LocalDateTime.now());

        Quiz quiz = attempt.getQuiz();
        List<Question> quizQuestions = quiz.getQuestions();
        Map<Long, Question> questionMap = quizQuestions.stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        final int[] score = {0};
        List<AttemptAnswer> newAttemptAnswers = submissionRequest.getAnswers().stream().map(answerDto -> {
            Question question = questionMap.get(answerDto.getQuestionId());
            if (question == null) {
                throw new RuntimeException("Question with id " + answerDto.getQuestionId() + " not found in quiz.");
            }

            AttemptAnswer attemptAnswer = new AttemptAnswer();
            attemptAnswer.setAttempt(attempt);
            attemptAnswer.setQuestion(question);
            attemptAnswer.setUserAnswer(answerDto.getUserAnswer());

            boolean isCorrect = false;
            if (question.getType() == QuestionType.MCQ) {
                isCorrect = question.getOptions().stream()
                        .anyMatch(option -> option.isCorrect() && option.getText().equalsIgnoreCase(answerDto.getUserAnswer()));
            } else if (question.getType() == QuestionType.SHORT_ANSWER) {
                isCorrect = question.getCorrectAnswer().equalsIgnoreCase(answerDto.getUserAnswer().trim());
            }
            attemptAnswer.setIsCorrect(isCorrect);
            if (isCorrect) {
                score[0]++;
            }
            return attemptAnswer;
        }).collect(Collectors.toList());

        attempt.setScore(score[0]);

        if (attempt.getAnswers() == null) {
            attempt.setAnswers(new java.util.ArrayList<>());
        }
        attempt.getAnswers().clear();
        attempt.getAnswers().addAll(newAttemptAnswers);

        for (AttemptAnswer ans : newAttemptAnswers) {
            ans.setAttempt(attempt);
        }

        Attempt savedAttempt = attemptRepository.save(attempt);
        return mapAttemptToResultDto(savedAttempt);
    }

    public List<AttemptResultDto> getAllAttemptHistory() {
        User currentUser = getCurrentAuthenticatedUser();
        List<Attempt> attempts;
        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            attempts = attemptRepository.findAll();
        } else {
            attempts = attemptRepository.findByUser(currentUser);
        }
        return attempts.stream()
                .map(this::mapAttemptToResultDto)
                .sorted(Comparator.comparing(AttemptResultDto::getStartTime).reversed())
                .collect(Collectors.toList());
    }

    public AttemptResultDto getAttemptDetails(Long attemptId) {
        User currentUser = getCurrentAuthenticatedUser();
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found with id: " + attemptId));

        if (currentUser.getRole() != Role.ROLE_ADMIN && !attempt.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: You are not authorized to view details for this attempt.");
        }
        return mapAttemptToResultDto(attempt);
    }

    private AttemptResultDto mapAttemptToResultDto(Attempt attempt) {
        AttemptResultDto dto = new AttemptResultDto();
        dto.setId(attempt.getId());
        dto.setQuizId(attempt.getQuiz().getId());
        dto.setQuizTitle(attempt.getQuiz().getTitle());
        dto.setStartTime(attempt.getStartTime());
        dto.setEndTime(attempt.getEndTime());
        dto.setScore(attempt.getScore());
        dto.setTotalQuestions(attempt.getQuiz().getQuestions().size());

        List<AttemptAnswerDetailDto> detailedAnswers = attempt.getAnswers().stream()
                .map(attemptAnswer -> {
                    Question question = attemptAnswer.getQuestion();
                    AttemptAnswerDetailDto detailDto = new AttemptAnswerDetailDto();
                    detailDto.setQuestionId(question.getId());
                    detailDto.setQuestionText(question.getText());
                    detailDto.setQuestionType(question.getType());
                    detailDto.setUserAnswer(attemptAnswer.getUserAnswer());
                    detailDto.setIsCorrect(attemptAnswer.getIsCorrect());

                    if (question.getType() == QuestionType.MCQ) {
                        detailDto.setOptions(question.getOptions().stream()
                                .map(o -> new com.encapsulearn.quiz_api.dto.OptionDto(o.getText(), o.isCorrect()))
                                .collect(Collectors.toList()));
                        question.getOptions().stream().filter(QuizOption::isCorrect)
                                .findFirst()
                                .ifPresent(option -> detailDto.setCorrectAnswer(option.getText()));
                    } else if (question.getType() == QuestionType.SHORT_ANSWER) {
                        detailDto.setCorrectAnswer(question.getCorrectAnswer());
                    }
                    return detailDto;
                })
                .collect(Collectors.toList());
        dto.setDetailedAnswers(detailedAnswers);
        return dto;
    }
}