package com.hackademics.dto;

import jakarta.validation.constraints.Size;

public class SubjectUpdateDto {
    private String subjectName; 

    @Size(min = 4, max = 4)
    private String subjectTag; 

    // Getters and setters

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
