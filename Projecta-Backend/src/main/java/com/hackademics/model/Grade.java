package com.hackademics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User user;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Getter
    @Setter
    @Column(name = "grade", nullable = false)
    private Double grade;
}