package com.hackademics.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class LabSectionResponseDto {
    private Long id;
    private Long sectionId;
    private Integer capacity;
    private Integer currentEnroll;
    private CourseResponseDto course;
    private Integer days;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startDate;
    private LocalDate endDate;

    public LabSectionResponseDto() {
    }

    public LabSectionResponseDto(Long id, Long sectionId, Integer capacity, Integer currentEnroll,
            CourseResponseDto course, Integer days, LocalTime startTime, LocalTime endTime,
            LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.sectionId = sectionId;
        this.capacity = capacity;
        this.currentEnroll = currentEnroll;
        this.course = course;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
    }

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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getCurrentEnroll() {
        return currentEnroll;
    }

    public void setCurrentEnroll(Integer currentEnroll) {
        this.currentEnroll = currentEnroll;
    }

    public CourseResponseDto getCourse() {
        return course;
    }

    public void setCourse(CourseResponseDto course) {
        this.course = course;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
} 