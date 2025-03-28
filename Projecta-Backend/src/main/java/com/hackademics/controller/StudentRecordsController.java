package com.hackademics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackademics.dto.StudentRecordDto;
import com.hackademics.service.StudentRecordsService;

@RestController
@RequestMapping("/api/student-records")
public class StudentRecordsController {

    private static final MediaType MEDIA_TYPE_CSV = new MediaType("text", "csv");

    @Autowired
    private StudentRecordsService studentRecordsService;

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentRecordDto> getStudentRecord(
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(studentRecordsService.getStudentRecord(studentId, currentUser));
    }

    @GetMapping("/{studentId}/text")
    public ResponseEntity<String> getStudentRecordAsText(
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails currentUser) {
        String content = studentRecordsService.getStudentRecordAsText(studentId, currentUser);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_record.txt")
                .body(content);
    }

    @GetMapping("/{studentId}/csv")
    public ResponseEntity<String> getStudentRecordAsCsv(
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails currentUser) {
        String content = studentRecordsService.getStudentRecordAsCsv(studentId, currentUser);
        return ResponseEntity.ok()
                .contentType(MEDIA_TYPE_CSV)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_record.csv")
                .body(content);
    }
} 