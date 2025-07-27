package com.encapsulearn.quiz_api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResultDto {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer score; // Score obtained
    private Integer totalQuestions; // Total questions in quiz
    private List<AttemptAnswerDetailDto> detailedAnswers; // For detailed history
}
