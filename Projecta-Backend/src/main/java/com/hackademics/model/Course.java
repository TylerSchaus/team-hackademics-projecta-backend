package com.hackademics.model;

import java.time.LocalDateTime;
import java.util.ArrayList; 
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Getter
    @Setter
    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Getter
    @Setter
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Getter
    @Setter
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Getter
    @Setter
    @Column(name = "enroll_limit", nullable = false)
    private int enrollLimit;

    @Getter
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Grade> grades = new ArrayList<>();

    @Getter
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Enrollment> enrollments = new ArrayList<>();

}