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
import com.hackademics.controller.WaitlistController;
import com.hackademics.model.Course;
import com.hackademics.model.Subject;
import com.hackademics.model.Waitlist;
import com.hackademics.service.WaitlistService;

@WebMvcTest(WaitlistController.class)
public class WaitlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WaitlistService waitlistService;

    @Autowired
    private ObjectMapper objectMapper;

    private Waitlist testWaitlist;
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
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createWaitlist_AdminUser_Success() throws Exception {
        when(waitlistService.saveWaitlist(any(Waitlist.class))).thenReturn(testWaitlist);

        mockMvc.perform(post("/api/waitlists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testWaitlist)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.waitlistLimit").value(50))
                .andExpect(jsonPath("$.course.id").value(1));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void createWaitlist_NonAdminUser_Forbidden() throws Exception {
        mockMvc.perform(post("/api/waitlists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testWaitlist)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllWaitlists_AdminUser_Success() throws Exception {
        List<Waitlist> waitlists = Arrays.asList(testWaitlist);
        when(waitlistService.getAllWaitlists()).thenReturn(waitlists);

        mockMvc.perform(get("/api/waitlists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].waitlistLimit").value(50));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getAllWaitlists_NonAdminUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/waitlists"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getWaitlistById_AdminUser_Success() throws Exception {
        when(waitlistService.getWaitlistById(1L)).thenReturn(testWaitlist);

        mockMvc.perform(get("/api/waitlists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.waitlistLimit").value(50))
                .andExpect(jsonPath("$.course.id").value(1));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getWaitlistById_NonAdminUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/waitlists/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateWaitlist_AdminUser_Success() throws Exception {
        when(waitlistService.updateWaitlist(any(Waitlist.class))).thenReturn(testWaitlist);

        mockMvc.perform(put("/api/waitlists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testWaitlist)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.waitlistLimit").value(50));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void updateWaitlist_NonAdminUser_Forbidden() throws Exception {
        mockMvc.perform(put("/api/waitlists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testWaitlist)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteWaitlist_AdminUser_Success() throws Exception {
        mockMvc.perform(delete("/api/waitlists/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void deleteWaitlist_NonAdminUser_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/waitlists/1"))
                .andExpect(status().isForbidden());
    }
} 