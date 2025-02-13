package com.hackademics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    @JoinColumn(name = "student_id")
    private Long studentId;

    @Getter
    @Setter
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Getter
    @Setter
    @Column(name = "grade", nullable = false)
    private Double grade;
}