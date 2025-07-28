package com.encapsulearn.quiz_api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptStartResponseDto {
    private Long id; // We only need the ID of the new attempt
}