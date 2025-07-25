package com.encapsulearn.quiz_api.entity;

import com.encapsulearn.quiz_api.enums.QuizType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private int durationMinutes; // Duration of the quiz in minutes

    @Enumerated(EnumType.STRING)
    private QuizType type; // MCQ, SHORT_ANSWER

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;
}
