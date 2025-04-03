package com.hackademics.dto.ResponseDto;

public class WaitlistRequestResponseDto {

    private Long id; 
    private WaitlistSummaryDto waitlist ; 
    private StudentSummaryDto student; 
    private CourseSummaryDto course; 
    
    public WaitlistRequestResponseDto() {
    }

    public WaitlistRequestResponseDto(Long id, WaitlistSummaryDto waitlist, StudentSummaryDto student, CourseSummaryDto course) {
        this.id = id;
        this.waitlist = waitlist;
        this.student = student;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public WaitlistSummaryDto getWaitlist() {
        return waitlist;
    }

    public StudentSummaryDto getStudent() {
        return student;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setWaitlist(WaitlistSummaryDto waitlist) {
        this.waitlist = waitlist;
    }
    
    public void setStudent(StudentSummaryDto student) {
        this.student = student;
    }

    public void setCourse(CourseSummaryDto course) {
        this.course = course;
    }   

    public CourseSummaryDto getCourse() {
        return course;
    }
    
    

}
