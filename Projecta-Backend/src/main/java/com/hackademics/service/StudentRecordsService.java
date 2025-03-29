package com.hackademics.service;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.StudentRecordDto;

public interface StudentRecordsService {
    StudentRecordDto getStudentRecord(Long studentId, UserDetails currentUser);
    String getStudentRecordAsText(Long studentId, UserDetails currentUser);
    String getStudentRecordAsCsv(Long studentId, UserDetails currentUser);
}