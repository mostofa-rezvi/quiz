package com.encapsulearn.quiz_api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptSubmissionRequest {
    private List<AttemptAnswerDto> answers;
}

