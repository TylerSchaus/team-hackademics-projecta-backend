package com.hackademics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "student_reference", nullable = false)
    private User student;

    @Column(name = "student_id")
    private Long studentId;

    @ManyToOne
    @JoinColumn(name = "lab_section_id", nullable = true)
    private LabSection labSection;

    @Column(name = "term")
    private String term;

    // Constructor

    public Enrollment(){

    }

    public Enrollment(Course course, User student, LabSection labSection) {
        this.course = course;
        this.student = student;
        this.studentId = student.getStudentId();
        this.labSection = labSection;
        this.term = course.getTerm();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
        this.studentId = student.getStudentId();
    }

    public Long getStudentId() {
        return studentId;
    }

    public LabSection getLabSection() {
        return labSection;
    }

    public void setLabSection(LabSection labSection) {
        this.labSection = labSection;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}