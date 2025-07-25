package com.encapsulearn.quiz_api.repository;

import com.encapsulearn.quiz_api.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {}
