package com.hackademics.dto.ResponseDto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.hackademics.model.OfferingType;


public class CourseResponseDto {
    private Long id;
    private AdminSummaryDto admin;
    private SubjectResponseDto subject;
    private String courseName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer enrollLimit;
    private Integer currentEnroll;
    private String courseNumber;
    private String courseTag;
    private String term;
    private Integer days;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer numLabSection;
    private WaitlistSummaryDto waitlist;
    private OfferingType offeringType;

    public CourseResponseDto() {
    }

    public CourseResponseDto(Long id, AdminSummaryDto admin, SubjectResponseDto subject, String courseName,
            LocalDate startDate, LocalDate endDate, Integer enrollLimit, Integer currentEnroll,
            String courseNumber, String courseTag, String term, Integer days,
            LocalTime startTime, LocalTime endTime, Integer numLabSection) {
        this.id = id;
        this.admin = admin;
        this.subject = subject;
        this.courseName = courseName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.enrollLimit = enrollLimit;
        this.currentEnroll = currentEnroll;
        this.courseNumber = courseNumber;
        this.courseTag = courseTag;
        this.term = term;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numLabSection = numLabSection;
        this.waitlist = null; 
        this.offeringType = OfferingType.COURSE;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AdminSummaryDto getAdmin() {
        return admin;
    }

    public void setAdmin(AdminSummaryDto admin) {
        this.admin = admin;
    }

    public SubjectResponseDto getSubject() {
        return subject;
    }

    public void setSubject(SubjectResponseDto subject) {
        this.subject = subject;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    public Integer getEnrollLimit() {
        return enrollLimit;
    }

    public void setEnrollLimit(Integer enrollLimit) {
        this.enrollLimit = enrollLimit;
    }

    public Integer getCurrentEnroll() {
        return currentEnroll;
    }

    public void setCurrentEnroll(Integer currentEnroll) {
        this.currentEnroll = currentEnroll;
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

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
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

    public Integer getNumLabSection() {
        return numLabSection;
    }

    public void setNumLabSection(Integer numLabSection) {
        this.numLabSection = numLabSection;
    }

    public WaitlistSummaryDto getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(WaitlistSummaryDto waitlist) {
        this.waitlist = waitlist;
        if (waitlist != null) {
            this.offeringType = OfferingType.WAITLIST;
        } else {
            this.offeringType = OfferingType.COURSE;
        }
    }

    public OfferingType getOfferingType() {
        return offeringType;
    }

    public void setOfferingType(OfferingType offeringType) {
        this.offeringType = offeringType;
    }
} 