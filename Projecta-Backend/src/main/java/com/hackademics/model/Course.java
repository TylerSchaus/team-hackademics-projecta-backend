package com.hackademics.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User admin;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "enroll_limit", nullable = false)
    private int enrollLimit;

    @Column(name = "current_enroll", nullable = false)
    private int currentEnroll;

    @Column(name = "course_number", nullable = false)
    private String courseNumber;

    @Column(name = "course_tag", nullable = false)
    private String courseTag;

    @Column(name = "semester", nullable = false)
    private int semester;

    @Column(name = "days") // Allow null values for asynchronous classes. 
    private Integer days;  

    @Column(name = "start_time")
    private LocalTime startTime; 

    @Column(name = "end_time")
    private LocalTime endTime;  

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final List<Grade> grades = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final List<Enrollment> enrollments = new ArrayList<>();

    // Constructor
    public Course(User admin, Subject subject, String courseName, LocalDateTime startDate, LocalDateTime endDate, int enrollLimit,
            String courseNumber, Integer days, LocalTime startTime, LocalTime endTime) {
        this.admin = admin;
        this.subject = subject;
        this.courseName = courseName;
        this.startDate = startDate;
        if (null == startDate.getMonth()) {
            semester = 3;
        } else {
            semester = switch (startDate.getMonth()) {
                case SEPTEMBER ->
                    1;
                case JANUARY ->
                    2;
                default ->
                    3;
            };
        }
        this.endDate = endDate;
        this.enrollLimit = enrollLimit;
        this.courseNumber = courseNumber;
        this.courseTag = subject.getSubjectTag() + " " + courseNumber;
        this.currentEnroll = 0;
        this.days = days; 
        this.startTime = startTime; 
        this.endTime = endTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getEnrollLimit() {
        return enrollLimit;
    }

    public void setEnrollLimit(int enrollLimit) {
        this.enrollLimit = enrollLimit;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getCourseTag() {
        return courseTag;
    }

    public void setCourseTag(String courseTag) {
        this.courseTag = courseTag;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getCurrentEnroll() {
        return currentEnroll;
    }

    public void setCurrentEnroll(int currentEnroll) {
        this.currentEnroll = currentEnroll;
    }

    public Integer getDays() {
        return days;
    }
    
    public void setDays(Integer days) {
        this.days = days;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    

}
