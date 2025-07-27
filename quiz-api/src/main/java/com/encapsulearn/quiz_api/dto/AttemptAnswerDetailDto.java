package com.encapsulearn.quiz_api.dto;

import com.encapsulearn.quiz_api.enums.QuestionType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptAnswerDetailDto {
    private Long questionId;
    private String questionText;
    private QuestionType questionType;
    private String userAnswer;
    private String correctAnswer; // For short answer
    private List<OptionDto> options; // For MCQ
    private Boolean isCorrect; // Whether user's answer was correct
}
