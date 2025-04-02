package com.hackademics.dto.ResponseDto;

public class WaitlistRequestResponseDto {

    private Long id; 
    private WaitlistSummaryDto waitlist ; 
    private StudentSummaryDto student; 
    
    public WaitlistRequestResponseDto() {
    }

    public WaitlistRequestResponseDto(Long id, WaitlistSummaryDto waitlist, StudentSummaryDto student) {
        this.id = id;
        this.waitlist = waitlist;
        this.student = student;
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
    
    

}
