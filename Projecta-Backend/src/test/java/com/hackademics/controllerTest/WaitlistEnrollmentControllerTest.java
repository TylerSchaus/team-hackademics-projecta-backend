package com.hackademics.controllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackademics.controller.WaitlistEnrollmentController;
import com.hackademics.model.Course;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.service.WaitlistEnrollmentService;

@WebMvcTest(WaitlistEnrollmentController.class)
public class WaitlistEnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WaitlistEnrollmentService waitlistEnrollmentService;
    
    @MockBean
    private WaitlistRepository waitlistRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private WaitlistEnrollment testEnrollment;
    private Waitlist testWaitlist;
    private User testStudent;
    private Course testCourse;
    private Subject testSubject;

    @BeforeEach
    void setUp() {
        testSubject = new Subject("Computer Science", "COSC");
        testSubject.setId(1L);
        
        testCourse = new Course(null, testSubject, "Introduction to Programming", null, null, 100, "121", null, null, null);
        testCourse.setId(1L);
        
        testWaitlist = new Waitlist(testCourse, 50);
        testWaitlist.setId(1L);
        
        testStudent = new User("John", "Doe", "john@example.com", "password", Role.STUDENT, 1L);
        testStudent.setId(1L);
        
        testEnrollment = new WaitlistEnrollment(1, testWaitlist, testStudent);
        testEnrollment.setId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createWaitlistEnrollment_AdminUser_Success() throws Exception {
        when(waitlistRepository.findById(1L)).thenReturn(java.util.Optional.of(testWaitlist));
        when(waitlistEnrollmentService.getAllWaitlistEnrollments()).thenReturn(Arrays.asList());
        when(waitlistEnrollmentService.saveWaitlistEnrollment(any(WaitlistEnrollment.class))).thenReturn(testEnrollment);

        mockMvc.perform(post("/api/waitlist-enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEnrollment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.waitlistPosition").value(1))
                .andExpect(jsonPath("$.student.id").value(1));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void createWaitlistEnrollment_StudentUser_Success() throws Exception {
        when(waitlistRepository.findById(1L)).thenReturn(java.util.Optional.of(testWaitlist));
        when(waitlistEnrollmentService.getAllWaitlistEnrollments()).thenReturn(Arrays.asList());
        when(waitlistEnrollmentService.saveWaitlistEnrollment(any(WaitlistEnrollment.class))).thenReturn(testEnrollment);

        mockMvc.perform(post("/api/waitlist-enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEnrollment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.waitlistPosition").value(1))
                .andExpect(jsonPath("$.student.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllWaitlistEnrollments_AdminUser_Success() throws Exception {
        List<WaitlistEnrollment> enrollments = Arrays.asList(testEnrollment);
        when(waitlistEnrollmentService.getAllWaitlistEnrollments()).thenReturn(enrollments);

        mockMvc.perform(get("/api/waitlist-enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].waitlistPosition").value(1));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getAllWaitlistEnrollments_NonAdminUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/waitlist-enrollments"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getWaitlistEnrollmentById_AdminUser_Success() throws Exception {
        when(waitlistEnrollmentService.getWaitlistEnrollmentById(1L)).thenReturn(testEnrollment);

        mockMvc.perform(get("/api/waitlist-enrollments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.waitlistPosition").value(1))
                .andExpect(jsonPath("$.student.id").value(1));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getWaitlistEnrollmentById_NonAdminUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/waitlist-enrollments/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteWaitlistEnrollment_AdminUser_Success() throws Exception {
        when(waitlistEnrollmentService.getWaitlistEnrollmentById(1L)).thenReturn(testEnrollment);

        mockMvc.perform(delete("/api/waitlist-enrollments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void deleteWaitlistEnrollment_StudentUser_Success() throws Exception {
        when(waitlistEnrollmentService.getWaitlistEnrollmentById(1L)).thenReturn(testEnrollment);

        mockMvc.perform(delete("/api/waitlist-enrollments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getWaitlistEnrollmentsByCourseId_AdminUser_Success() throws Exception {
        List<WaitlistEnrollment> enrollments = Arrays.asList(testEnrollment);
        when(waitlistEnrollmentService.getAllWaitlistEnrollments()).thenReturn(enrollments);

        mockMvc.perform(get("/api/waitlist-enrollments/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].waitlist.id").value(1))
                .andExpect(jsonPath("$[0].waitlist.course.id").value(1));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getWaitlistEnrollmentsByCourseId_NonAdminUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/waitlist-enrollments/course/1"))
                .andExpect(status().isForbidden());
    }
} 