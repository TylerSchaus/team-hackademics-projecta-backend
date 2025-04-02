package com.hackademics.dto.RequestDto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public class LabSectionDto {

    @NotNull 
    private Long courseId; 

    @NotNull 
    private Integer capacity;

    @NotNull
    private Integer days; // Bitmap for days of the week

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    public LabSectionDto(Long courseId, Integer capacity, Integer days, LocalTime startTime, LocalTime endTime) {
        this.courseId = courseId;
        this.capacity = capacity;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
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
