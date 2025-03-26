package com.hackademics.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class LabSectionResponseDto {
    private Long id;
    private String sectionId;
    private Integer capacity;
    private Integer currentEnroll;
    private CourseResponseDto course;
    private List<String> days;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startDate;
    private LocalDate endDate;

    public LabSectionResponseDto() {
    }

    public LabSectionResponseDto(Long id, String sectionId, Integer capacity, Integer currentEnroll,
            CourseResponseDto course, List<String> days, LocalTime startTime, LocalTime endTime,
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

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
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

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
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