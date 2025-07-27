package com.encapsulearn.quiz_api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptAnswerDto {
    private Long questionId;
    private String userAnswer;
}

