package com.encapsulearn.quiz_api.dto;

import com.encapsulearn.quiz_api.enums.QuizType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDto {
    private Long id;
    private String title;
    private int durationMinutes;
    private QuizType type;
    private int numberOfQuestions;
}
