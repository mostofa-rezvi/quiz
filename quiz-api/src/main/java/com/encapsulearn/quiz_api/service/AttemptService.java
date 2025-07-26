package com.encapsulearn.quiz_api.service;

import com.encapsulearn.quiz_api.dto.AttemptAnswerDetailDto;
import com.encapsulearn.quiz_api.dto.AttemptResultDto;
import com.encapsulearn.quiz_api.dto.AttemptSubmissionRequest;
import com.encapsulearn.quiz_api.entity.*;
import com.encapsulearn.quiz_api.enums.QuestionType;
import com.encapsulearn.quiz_api.repository.AttemptAnswerRepository;
import com.encapsulearn.quiz_api.repository.AttemptRepository;
import com.encapsulearn.quiz_api.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
    private QuizService quizService; // To get Quiz details
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AttemptAnswerRepository attemptAnswerRepository;


    @Transactional
    public Attempt startAttempt(Long quizId) {
        Quiz quiz = quizService.getQuizById(quizId);
        Attempt attempt = new Attempt();
        attempt.setQuiz(quiz);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setScore(0); // Initialize score
        return attemptRepository.save(attempt);
    }

    @Transactional
    public AttemptResultDto submitAttempt(Long attemptId, AttemptSubmissionRequest submissionRequest) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found with id: " + attemptId));

        if (attempt.getEndTime() != null) {
            throw new RuntimeException("Attempt already submitted.");
        }

        attempt.setEndTime(LocalDateTime.now());

        Quiz quiz = attempt.getQuiz();
        List<Question> quizQuestions = quiz.getQuestions();
        Map<Long, Question> questionMap = quizQuestions.stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        final int[] score = {0};
        List<AttemptAnswer> attemptAnswers = submissionRequest.getAnswers().stream().map(answerDto -> {
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
                // Check if the user's answer matches the text of the correct option
                // Assuming userAnswer for MCQ is the text of the chosen option
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
        attempt.setAnswers(attemptAnswers); // This will persist answers due to cascade

        Attempt savedAttempt = attemptRepository.save(attempt);
        return mapAttemptToResultDto(savedAttempt);
    }

    // --- Get All Attempt History ---
    public List<AttemptResultDto> getAllAttemptHistory() {
        return attemptRepository.findAll().stream()
                .map(this::mapAttemptToResultDto)
                .sorted(Comparator.comparing(AttemptResultDto::getStartTime).reversed()) // Newest first
                .collect(Collectors.toList());
    }

    // --- Get Detailed Attempt History by ID ---
    public AttemptResultDto getAttemptDetails(Long attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found with id: " + attemptId));
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
                        // For MCQ, correct answer is implicitly in options with correct=true
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
