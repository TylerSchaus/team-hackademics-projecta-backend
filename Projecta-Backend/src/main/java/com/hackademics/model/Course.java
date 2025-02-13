package com.hackademics.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @Column(name = "course_id", length = 5)
    private String courseId;

    @Column(name = "professor_id", length = 5, nullable = false)
    private String professorId;

    @Column(name = "subject_id", nullable = false)
    private int subjectId;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "enroll_limit", nullable = false)
    private int enrollLimit;
}