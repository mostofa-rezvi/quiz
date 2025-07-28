package com.encapsulearn.quiz_api.service;

import com.encapsulearn.quiz_api.dto.*;
import com.encapsulearn.quiz_api.entity.*;
import com.encapsulearn.quiz_api.enums.QuestionType;
import com.encapsulearn.quiz_api.repository.QuizRepository;
import com.encapsulearn.quiz_api.util.ExcelHelper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Transactional
    public QuizResponseDto createQuizManually(QuizCreateRequest request) {
        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDurationMinutes(request.getDurationMinutes());
        quiz.setType(request.getType());

        List<Question> questions = request.getQuestions().stream().map(qDto -> {
            Question question = new Question();
            question.setText(qDto.getText());
            question.setType(qDto.getType());
            question.setQuiz(quiz);

            if (qDto.getType() == QuestionType.MCQ) {
                List<QuizOption> options = qDto.getOptions().stream().map(oDto -> {
                    QuizOption option = new QuizOption();
                    option.setText(oDto.getText());
                    option.setCorrect(oDto.isCorrect());
                    option.setQuestion(question);
                    return option;
                }).collect(Collectors.toList());
                question.setOptions(options);
            } else if (qDto.getType() == QuestionType.SHORT_ANSWER) {
                question.setCorrectAnswer(qDto.getCorrectAnswer());
            }
            return question;
        }).collect(Collectors.toList());

        quiz.setQuestions(questions);
        Quiz savedQuiz = quizRepository.save(quiz);

        return new QuizResponseDto(
                savedQuiz.getId(),
                savedQuiz.getTitle(),
                savedQuiz.getDurationMinutes(),
                savedQuiz.getType(),
                savedQuiz.getQuestions().size()
        );
    }

    @Transactional
    public List<QuizResponseDto> createQuizzesFromExcel(MultipartFile file) throws IOException {
        List<Quiz> quizzes = ExcelHelper.parseExcelToQuizzes(file.getInputStream());
        List<Quiz> savedQuizzes = quizRepository.saveAll(quizzes);
        return savedQuizzes.stream()
                .map(quiz -> new QuizResponseDto(
                        quiz.getId(),
                        quiz.getTitle(),
                        quiz.getDurationMinutes(),
                        quiz.getType(),
                        quiz.getQuestions().size()
                ))
                .collect(Collectors.toList());
    }

    public List<QuizResponseDto> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(quiz -> new QuizResponseDto(
                        quiz.getId(),
                        quiz.getTitle(),
                        quiz.getDurationMinutes(),
                        quiz.getType(),
                        quiz.getQuestions().size()
                ))
                .collect(Collectors.toList());
    }

    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
    }

    @Transactional
    public QuizResponseDto updateQuiz(Long id, QuizCreateRequest request) {
        Quiz existingQuiz = getQuizById(id);
        existingQuiz.setTitle(request.getTitle());
        existingQuiz.setDurationMinutes(request.getDurationMinutes());
        existingQuiz.setType(request.getType());

        existingQuiz.getQuestions().clear();
        request.getQuestions().forEach(qDto -> {
            Question question = new Question();
            question.setText(qDto.getText());
            question.setType(qDto.getType());
            question.setQuiz(existingQuiz);

            if (qDto.getType() == QuestionType.MCQ) {
                List<QuizOption> options = qDto.getOptions().stream().map(oDto -> {
                    QuizOption option = new QuizOption();
                    option.setText(oDto.getText());
                    option.setCorrect(oDto.isCorrect());
                    option.setQuestion(question);
                    return option;
                }).collect(Collectors.toList());
                question.setOptions(options);
            } else if (qDto.getType() == QuestionType.SHORT_ANSWER) {
                question.setCorrectAnswer(qDto.getCorrectAnswer());
            }
            existingQuiz.getQuestions().add(question);
        });

        Quiz updatedQuiz = quizRepository.save(existingQuiz);
        return new QuizResponseDto(
                updatedQuiz.getId(),
                updatedQuiz.getTitle(),
                updatedQuiz.getDurationMinutes(),
                updatedQuiz.getType(),
                updatedQuiz.getQuestions().size()
        );
    }

    @Transactional
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new RuntimeException("Quiz not found with id: " + id);
        }
        quizRepository.deleteById(id);
    }
}