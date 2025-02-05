package com.hackademics.Model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Student {
    
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long studentId;

    @Column(nullable = false)
    private Date birthDate;

    @Column(nullable = false)
    private Date enrollmentDate;

    @Column
    private Date expectedGradDate;

    // Getters and setters for each field
                
    public long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Date getExpectedGradDate() {
        return expectedGradDate;
    }

    public void setExpectedGradDate(Date expectedGradDate) {
        this.expectedGradDate = expectedGradDate;
    }
}
