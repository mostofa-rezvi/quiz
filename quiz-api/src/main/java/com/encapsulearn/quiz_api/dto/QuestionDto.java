package com.encapsulearn.quiz_api.dto;

import com.encapsulearn.quiz_api.enums.QuestionType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Long id; // Used for update/retrieval
    private String text;
    private QuestionType type;
    private List<OptionDto> options; // For MCQ
    private String correctAnswer; // For SHORT_ANSWER
}
