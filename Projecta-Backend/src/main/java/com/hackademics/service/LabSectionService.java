package com.hackademics.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.LabSectionDto; 
import com.hackademics.dto.LabSectionUpdateDto;
import com.hackademics.model.LabSection;
 

public interface LabSectionService {

    List<LabSection> findByCourseId(Long id);
    LabSection createLabSection(LabSectionDto labSectionDto, UserDetails userDetails);
    LabSection getLabSectionById(Long id); 
    LabSection updateLabSection(LabSectionUpdateDto updateLabSectionDto, UserDetails userDetails); 
    void deleteLabSection(Long id); 
    
}
