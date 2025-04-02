package com.hackademics.utilTests;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.hackademics.dto.ResponseDto.CourseResponseDto;
import com.hackademics.dto.ResponseDto.GradeResponseDto;
import com.hackademics.dto.ResponseDto.LabSectionResponseDto;
import com.hackademics.dto.ResponseDto.SubjectResponseDto;
import com.hackademics.dto.ResponseDto.UserResponseDTO;
import com.hackademics.dto.ResponseDto.WaitlistEnrollmentResponseDto;
import com.hackademics.dto.ResponseDto.WaitlistResponseDto;
import com.hackademics.model.Course;
import com.hackademics.model.Grade;
import com.hackademics.model.LabSection;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.util.ConvertToResponseDto;

@SpringBootTest
@ActiveProfiles("test")
class ConvertToResponseDtoTest {

    private User admin;
    private User student;
    private Subject subject;
    private Course course;
    private LabSection labSection;
    private Grade grade;
    private Waitlist waitlist;
    private WaitlistEnrollment waitlistEnrollment;

    @BeforeEach
    void setUp() {
        // Create test admin
        admin = new User("Admin", "User", "admin@example.com", "password", Role.ADMIN, 100L);
        admin.setId(1L);

        // Create test student
        student = new User("Student", "User", "student@example.com", "password", Role.STUDENT, 200L);
        student.setId(2L);

        // Create test subject
        subject = new Subject("Computer Science", "COSC");
        subject.setId(1L);

        // Create test course
        course = new Course(admin, subject, "Introduction to Programming", 
            LocalDateTime.now(), LocalDateTime.now().plusMonths(4), 50, "101", 1, 
            LocalTime.of(9, 0), LocalTime.of(10, 30));
        course.setId(1L);
        course.setCurrentEnroll(30);
        course.setWaitlistAvailable(true);

        // Create test lab section
        labSection = new LabSection(1L, 20, course, 1, LocalTime.of(11, 0), LocalTime.of(12, 30));
        labSection.setId(1L);
        labSection.setCurrentEnroll(15);

        // Create test grade
        grade = new Grade(student, course, 85.5);
        grade.setId(1L);

        // Create test waitlist
        waitlist = new Waitlist(course, 10);
        waitlist.setId(1L);

        // Create test waitlist enrollment
        waitlistEnrollment = new WaitlistEnrollment(1, waitlist, student);
        waitlistEnrollment.setId(1L);
        waitlistEnrollment.setTerm("20241");
    }

    @Test
    void testConvertToSubjectResponseDto() {
        SubjectResponseDto dto = ConvertToResponseDto.convertToSubjectResponseDto(subject);
        
        assertEquals(subject.getId(), dto.getId());
        assertEquals(subject.getSubjectName(), dto.getSubjectName());
        assertEquals(subject.getSubjectTag(), dto.getSubjectTag());
    }

    @Test
    void testConvertToCourseResponseDto() {
        CourseResponseDto dto = ConvertToResponseDto.convertToCourseResponseDto(course, waitlist);
        
        assertEquals(course.getId(), dto.getId());
        assertEquals(course.getCourseName(), dto.getCourseName());
        assertEquals(course.getCourseNumber(), dto.getCourseNumber());
        assertEquals(course.getCourseTag(), dto.getCourseTag());
        assertEquals(course.getEnrollLimit(), dto.getEnrollLimit());
        assertEquals(course.getCurrentEnroll(), dto.getCurrentEnroll());
        assertEquals(course.getTerm(), dto.getTerm());
        assertEquals(course.getDays(), dto.getDays());
        assertEquals(course.getStartTime(), dto.getStartTime());
        assertEquals(course.getEndTime(), dto.getEndTime());
        assertEquals(course.getNumLabSections(), dto.getNumLabSection());
        
        // Check admin and subject DTOs
        assertEquals(admin.getId(), dto.getAdmin().getId());
        assertEquals(subject.getId(), dto.getSubject().getId());
    }

    @Test
    void testConvertToCourseResponseDtoWithWaitlist() {
        // Add a waitlist enrollment to test waitlist logic
        waitlist.getWaitlistEnrollments().add(waitlistEnrollment);
        
        course.setCurrentEnroll(course.getEnrollLimit());
        course.setWaitlistAvailable(true);
        CourseResponseDto dto = ConvertToResponseDto.convertToCourseResponseDto(course, waitlist);
        
        assertNotNull(dto.getWaitlist());
        assertEquals(waitlist.getId(), dto.getWaitlist().getWaitlistId());
        assertEquals(waitlist.getWaitlistLimit(), dto.getWaitlist().getCapacity());
        assertEquals(1, dto.getWaitlist().getCurrentEnroll());
    }

    @Test
    void testConvertWaitlistToResponseDto() {
        WaitlistResponseDto dto = ConvertToResponseDto.convertWaitlistToResponseDto(waitlist, course);
        
        assertEquals(waitlist.getId(), dto.getWaitlistId());
        assertEquals(waitlist.getWaitlistLimit(), dto.getCapacity());
        assertEquals(course.getId(), dto.getCourse().getId());
    }

    @Test
    void testConvertToGradeResponseDto() {
        GradeResponseDto dto = ConvertToResponseDto.convertToGradeResponseDto(grade);
        
        assertEquals(grade.getId(), dto.getId());
        assertEquals(grade.getGrade(), dto.getGrade());
        assertEquals(student.getId(), dto.getStudent().getId());
        assertEquals(course.getId(), dto.getCourse().getId());
    }

    @Test
    void testConvertToLabSectionResponseDto() {
        LabSectionResponseDto dto = ConvertToResponseDto.convertToLabSectionResponseDto(labSection);
        
        assertEquals(labSection.getId(), dto.getId());
        assertEquals(labSection.getSectionId(), dto.getSectionId());
        assertEquals(labSection.getCapacity(), dto.getCapacity());
        assertEquals(labSection.getCurrentEnroll(), dto.getCurrentEnroll());
        assertEquals(labSection.getDays(), dto.getDays());
        assertEquals(labSection.getStartTime(), dto.getStartTime());
        assertEquals(labSection.getEndTime(), dto.getEndTime());
        assertEquals(course.getId(), dto.getCourse().getId());
    }

    @Test
    void testConvertToUserResponseDto() {
        UserResponseDTO dto = ConvertToResponseDto.convertToUserResponseDto(student);
        
        assertEquals(student.getId(), dto.getId());
        assertEquals(student.getFirstName(), dto.getFirstName());
        assertEquals(student.getLastName(), dto.getLastName());
        assertEquals(student.getEmail(), dto.getEmail());
        assertEquals(student.getRole().toString(), dto.getRole());
        assertEquals(student.getStudentId(), dto.getStudentId());
        assertEquals(student.getAdminId(), dto.getAdminId());
    }

    @Test
    void testConvertToWaitlistEnrollmentResponseDto() {
        WaitlistEnrollmentResponseDto dto = ConvertToResponseDto.convertToWaitlistEnrollmentResponseDto(waitlistEnrollment);
        
        assertEquals(waitlistEnrollment.getId(), dto.getId());
        assertEquals(waitlistEnrollment.getWaitlistPosition(), dto.getWaitlistPosition());
        assertEquals(waitlist.getId(), dto.getWaitlistResponseDto().getWaitlistId());
        assertEquals(student.getId(), dto.getStudentSummaryDto().getId());
        assertEquals(waitlistEnrollment.getTerm(), dto.getTerm());
    }
} 