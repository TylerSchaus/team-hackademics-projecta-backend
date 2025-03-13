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
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "grade", nullable = false)
    private Double grade;

    @Column(name = "course_tag_copy", nullable = false) // To ensure that even if a course is deleted, a copy of the course (COSC121, BIOL117, etc) remains. 
    private String courseTagCopy; 

    @Column(name = "course_name_copy", nullable = false)
    private String courseNameCopy;

    // Constructor
    public Grade(User student, Course course, Double grade) {
        this.student = student;
        this.course = course;
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

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
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
}