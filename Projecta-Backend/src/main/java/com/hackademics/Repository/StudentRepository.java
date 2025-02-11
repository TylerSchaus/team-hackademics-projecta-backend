package com.hackademics.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackademics.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    //  query methods 

    Optional<Student> findByEmail(String email);
}