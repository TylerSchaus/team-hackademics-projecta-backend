package com.hackademics.dto.ResponseDto;

public class GradeResponseDto {
    private Long id; 
    private Double grade;
    private Long studentId;
    private String courseName; 
    private String courseTag; 

    public GradeResponseDto() {
    }

    public GradeResponseDto(Long id, Double grade, String courseName, String courseTag, Long studentId) {
        this.id = id;
        this.grade = grade;
        this.courseName = courseName;
        this.courseTag = courseTag;
        this.studentId = studentId;
    }

    public Long getId() {
        return id;
    }
    public Double getGrade() {
        return grade;
    }

    public String getCourseName(){
        return this.courseName; 
    }

    public String getCourseTag(){
        return this.courseTag; 
    }

    public Long getStudentId(){
        return this.studentId; 
    }   

    public void setStudentId(Long studentId){
        this.studentId = studentId; 
    }

}
