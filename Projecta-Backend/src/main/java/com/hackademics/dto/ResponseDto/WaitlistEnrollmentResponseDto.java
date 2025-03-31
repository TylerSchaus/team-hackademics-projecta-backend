package com.hackademics.dto.ResponseDto;


public class WaitlistEnrollmentResponseDto {
    private Long id;
    private Integer waitlistPosition;
    private WaitlistResponseDto waitlistResponseDto;
    private StudentSummaryDto studentSummaryDto;
    private String term;

    public WaitlistEnrollmentResponseDto(){}

    public WaitlistEnrollmentResponseDto(Long id, Integer waitlistPosition, WaitlistResponseDto waitlistResponseDto, StudentSummaryDto studentSummaryDto, String term){
        this.id = id;
        this.waitlistPosition = waitlistPosition;
        this.waitlistResponseDto = waitlistResponseDto;
        this.studentSummaryDto = studentSummaryDto;
        this.term = term;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Integer getWaitlistPosition(){
        return waitlistPosition;
    }

    public void setWaitlistPosition(Integer waitlistPosition){
        this.waitlistPosition = waitlistPosition;
    }

    public WaitlistResponseDto getWaitlistResponseDto(){
        return waitlistResponseDto;
    }

    public void setWaitlistResponseDto(WaitlistResponseDto waitlistResponseDto){
        this.waitlistResponseDto = waitlistResponseDto;
    }

    public StudentSummaryDto getStudentSummaryDto(){
        return studentSummaryDto;
    }

    public void setStudentSummaryDto(StudentSummaryDto studentSummaryDto){
        this.studentSummaryDto = studentSummaryDto;
    }

    public String getTerm(){
        return term;
    }

    public void setTerm(String term){
        this.term = term;
    }

} 