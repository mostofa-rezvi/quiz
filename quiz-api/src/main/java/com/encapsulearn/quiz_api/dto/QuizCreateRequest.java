package com.encapsulearn.quiz_api.dto;

import com.encapsulearn.quiz_api.enums.QuizType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizCreateRequest {
    private String title;
    private int durationMinutes;
    private QuizType type;
    private List<QuestionDto> questions;
}
