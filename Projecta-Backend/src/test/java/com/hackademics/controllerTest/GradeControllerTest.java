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
import com.hackademics.controller.GradeController;
import com.hackademics.model.Course;
import com.hackademics.model.Grade;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.service.GradeService;

@WebMvcTest(GradeController.class)
public class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GradeService gradeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Grade testGrade;
    private User testStudent;
    private Course testCourse;
    private Subject testSubject;

    @BeforeEach
    void setUp() {
        testSubject = new Subject("Computer Science", "COSC");
        testSubject.setId(1L);
        
        testCourse = new Course(null, testSubject, "Introduction to Programming", null, null, 100, "121", null, null, null);
        testCourse.setId(1L);
        
        testStudent = new User("John", "Doe", "john@example.com", "password", Role.STUDENT, 1L);
        testStudent.setId(1L);
        
        testGrade = new Grade(testStudent, testCourse, 85.0);
        testGrade.setId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createGrade_AdminUser_Success() throws Exception {
        when(gradeService.saveGrade(any(Grade.class))).thenReturn(testGrade);

        mockMvc.perform(post("/api/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGrade)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.grade").value(85.0))
                .andExpect(jsonPath("$.student.id").value(1))
                .andExpect(jsonPath("$.course.id").value(1));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void createGrade_NonAdminUser_Forbidden() throws Exception {
        mockMvc.perform(post("/api/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGrade)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllGrades_AdminUser_Success() throws Exception {
        List<Grade> grades = Arrays.asList(testGrade);
        when(gradeService.getAllGrades()).thenReturn(grades);

        mockMvc.perform(get("/api/grades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].grade").value(85.0));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getAllGrades_NonAdminUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/grades"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateGrade_AdminUser_Success() throws Exception {
        when(gradeService.updateGrade(any(Grade.class))).thenReturn(testGrade);

        mockMvc.perform(put("/api/grades/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.grade").value(85.0));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void updateGrade_NonAdminUser_Forbidden() throws Exception {
        mockMvc.perform(put("/api/grades/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGrade)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteGrade_AdminUser_Success() throws Exception {
        mockMvc.perform(delete("/api/grades/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void deleteGrade_NonAdminUser_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/grades/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getGradesByStudentId_AdminUser_Success() throws Exception {
        List<Grade> grades = Arrays.asList(testGrade);
        when(gradeService.getAllGrades()).thenReturn(grades);

        mockMvc.perform(get("/api/grades/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].student.id").value(1));
    }

    @Test
    @WithMockUser(username = "1", roles = "STUDENT")
    void getGradesByStudentId_StudentUser_Success() throws Exception {
        List<Grade> grades = Arrays.asList(testGrade);
        when(gradeService.getAllGrades()).thenReturn(grades);

        mockMvc.perform(get("/api/grades/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].student.id").value(1));
    }

    @Test
    @WithMockUser(username = "2", roles = "STUDENT")
    void getGradesByStudentId_StudentUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/grades/student/1"))
                .andExpect(status().isForbidden());
    }
} 