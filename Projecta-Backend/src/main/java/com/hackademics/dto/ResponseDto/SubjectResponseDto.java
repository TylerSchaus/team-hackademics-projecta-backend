package com.hackademics.dto.ResponseDto;

public class SubjectResponseDto {
    private Long id;
    private String subjectName;
    private String subjectTag;

    public SubjectResponseDto() {
    }

    public SubjectResponseDto(Long id, String subjectName, String subjectTag) {
        this.id = id;
        this.subjectName = subjectName;
        this.subjectTag = subjectTag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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