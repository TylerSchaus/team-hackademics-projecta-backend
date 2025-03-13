package com.hackademics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SubjectDto {

    @NotBlank 
    private String subjectName; 

    @NotBlank
    @Size(min = 4, max = 4) 
    private String subjectTag; 
    
    // Constructor

    public SubjectDto(String subjectName, String subjectTag) {
        this.subjectName = subjectName;
        this.subjectTag = subjectTag;
    }

    // Getters and Setters

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectTag() {
        return subjectTag;
    }

    public void setSubjectTag(String subjectTag) {
        this.subjectTag = subjectTag;
    }
}
