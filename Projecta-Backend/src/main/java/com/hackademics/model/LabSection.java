package com.hackademics.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class LabSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "section_id")
    private Long sectionId;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "current_enroll")
    private int currentEnroll;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "course_tag")
    private String courseTag;

    @Column(name = "days", nullable = false) // Bitmap for days of the week
    private int days;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    // Constructor
    public LabSection(Long sectionId, int capacity, Course course, int days, LocalTime startTime, LocalTime endTime) {
        this.sectionId = sectionId;
        this.capacity = capacity;
        this.course = course;
        this.courseTag = course.getCourseTag();
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = course.getStartDate(); // Same as course start date
        this.endDate = course.getEndDate();     // Same as course end date
        this.currentEnroll = 0;
    }

    // Default constructor
    public LabSection() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
        this.courseTag = course.getCourseTag();
        this.startDate = course.getStartDate(); // Update start date when course changes
        this.endDate = course.getEndDate();     // Update end date when course changes
    }

    public String getCourseTag() {
        return courseTag;
    }

    public void setCourseTag(String courseTag) {
        this.courseTag = courseTag;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
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

    public int getCurrentEnroll() {
        return currentEnroll;
    }
    public void setCurrentEnroll(int currentEnroll){
        this.currentEnroll = currentEnroll;
    }
}
