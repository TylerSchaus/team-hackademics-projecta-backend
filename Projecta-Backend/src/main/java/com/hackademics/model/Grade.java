package com.hackademics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "grade", nullable = false)
    private Double grade;

    @Column(name = "course_tag_copy", nullable = false) // To ensure that even if a course is deleted, a copy of the course (COSC121, BIOL117, etc) remains. 
    private String courseTagCopy; 

    @Column(name = "course_name_copy", nullable = false)
    private String courseNameCopy;

    // Default constructor required by JPA
    public Grade() {
    }

    // Constructor
    public Grade(User student, Course course, Double grade) {
        this.studentId = student.getStudentId();
        this.courseId = course.getId();
        this.grade = grade;
        this.courseTagCopy = course.getCourseTag(); 
        this.courseNameCopy = course.getCourseName();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public String getCourseTagCopy() {
        return courseTagCopy;
    }

    public void setCourseTagCopy(String courseTagCopy) {
        this.courseTagCopy = courseTagCopy;
    }

    public String getCourseNameCopy() {
        return this.courseNameCopy;
    }

    public void setCourseNameCopy(String courseNameCopy) {
        this.courseNameCopy = courseNameCopy;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}