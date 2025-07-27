package com.encapsulearn.quiz_api.repository;

import com.encapsulearn.quiz_api.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {}
