package com.hackademics.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Getter
    @Setter
    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Getter
    @Setter
    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Getter
    @Setter
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Getter
    @Setter
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Getter
    @Setter
    @Column(name = "enroll_limit", nullable = false)
    private int enrollLimit;
}