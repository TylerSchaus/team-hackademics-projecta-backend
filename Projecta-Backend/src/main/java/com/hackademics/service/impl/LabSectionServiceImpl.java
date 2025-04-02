package com.hackademics.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.RequestDto.LabSectionDto;
import com.hackademics.dto.ResponseDto.LabSectionResponseDto;
import com.hackademics.model.Course;
import com.hackademics.model.LabSection;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.LabSectionRepository;
import com.hackademics.service.LabSectionService;
import com.hackademics.util.ConvertToResponseDto;
import com.hackademics.util.RoleBasedAccessVerification;

@Service
public class LabSectionServiceImpl implements LabSectionService {

    @Autowired
    private LabSectionRepository labSectionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private RoleBasedAccessVerification roleBasedAccessVerification;


    @Override
    public List<LabSectionResponseDto> findByCourseId(Long courseId) {

        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found.");
        }
        return labSectionRepository.findByCourseId(courseId).stream()
            .map(ConvertToResponseDto::convertToLabSectionResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public LabSectionResponseDto createLabSection(LabSectionDto labSectionDto, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create lab sections.");
        }

        Course course = courseRepository.findById(labSectionDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found."));

        Long sectionId = generateNextLabSectionId(labSectionDto.getCourseId());

        if (labSectionDto.getCapacity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Capacity must be greater than 0.");
        }

        if (labSectionDto.getStartTime().isAfter(labSectionDto.getEndTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be before end time.");
        }

        // Use the constructor to create the LabSection
        LabSection labSection = new LabSection(
                sectionId,
                labSectionDto.getCapacity(),
                course,
                labSectionDto.getDays(),
                labSectionDto.getStartTime(),
                labSectionDto.getEndTime()
        );

        course.setNumLabSections(course.getNumLabSections() + 1);
        courseRepository.save(course);

        return ConvertToResponseDto.convertToLabSectionResponseDto(labSectionRepository.save(labSection));
    }

    @Override
    public LabSectionResponseDto getLabSectionById(Long id) {
        return labSectionRepository.findById(id)
                .map(ConvertToResponseDto::convertToLabSectionResponseDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab section not found."));
    }

    // Utility methods 
    private Long generateNextLabSectionId(Long courseId) { // Ensures unique incrementing setion ids. 
        Long maxLabSectionId = labSectionRepository.findMaxLabSectionIdForCourse(courseId);
        return (maxLabSectionId != null) ? maxLabSectionId + 1 : 1L;
    }

}
