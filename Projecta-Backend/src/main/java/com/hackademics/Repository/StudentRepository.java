package com.hackademics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackademics.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    //  query methods 
}