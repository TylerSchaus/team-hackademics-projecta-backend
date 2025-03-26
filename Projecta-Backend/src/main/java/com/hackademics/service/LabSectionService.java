package com.hackademics.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.LabSectionDto;
import com.hackademics.dto.LabSectionResponseDto;
import com.hackademics.dto.LabSectionUpdateDto;

public interface LabSectionService {
    List<LabSectionResponseDto> findByCourseId(Long id);
    LabSectionResponseDto createLabSection(LabSectionDto labSectionDto, UserDetails userDetails);
    LabSectionResponseDto getLabSectionById(Long id);
    LabSectionResponseDto updateLabSection(LabSectionUpdateDto updateLabSectionDto, UserDetails userDetails);
    void deleteLabSection(Long id);
}
