package com.hackademics.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.LabSectionDto;
import com.hackademics.dto.LabSectionUpdateDto;
import com.hackademics.model.Course;
import com.hackademics.model.LabSection;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.LabSectionRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.LabSectionService;

@Service
public class LabSectionServiceImpl implements LabSectionService {

    @Autowired
    private LabSectionRepository labSectionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<LabSection> findByCourseId(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found.");
        }
        return labSectionRepository.findByCourseId(courseId);
    }

    @Override
    public LabSection createLabSection(LabSectionDto labSectionDto, UserDetails userDetails) {
        User authenticatedUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create lab sections.");
        }

        Course course = courseRepository.findById(labSectionDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found."));

        Long sectionId = generateNextLabSectionId(labSectionDto.getCourseId());

        // Use the constructor to create the LabSection
        LabSection labSection = new LabSection(
                sectionId,
                labSectionDto.getCapacity(),
                course,
                labSectionDto.getDays(),
                labSectionDto.getStartTime(),
                labSectionDto.getEndTime()
        );

        return labSectionRepository.save(labSection);
    }

    @Override
    public LabSection getLabSectionById(Long id) {
        return labSectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab section not found."));
    }

    @Override
    public LabSection updateLabSection(LabSectionUpdateDto labSectionUpdateDto, UserDetails userDetails) {
        User authenticatedUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can update lab sections.");
        }

        LabSection labSection = labSectionRepository.findById(labSectionUpdateDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab section not found."));

        if (labSectionUpdateDto.getCourseId() != null) {
            Course course = courseRepository.findById(labSectionUpdateDto.getCourseId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "New course not found."));
            labSection.setCourse(course);
            labSection.setStartDate(course.getStartDate()); // Update start date to match course
            labSection.setEndDate(course.getEndDate());     // Update end date to match course
        }

        if (labSectionUpdateDto.getCapacity() != null) {
            labSection.setCapacity(labSectionUpdateDto.getCapacity());
        }

        if (labSectionUpdateDto.getDays() != null) {
            labSection.setDays(labSectionUpdateDto.getDays());
        }

        if (labSectionUpdateDto.getStartTime() != null) {
            labSection.setStartTime(labSectionUpdateDto.getStartTime());
        }

        if (labSectionUpdateDto.getEndTime() != null) {
            labSection.setEndTime(labSectionUpdateDto.getEndTime());
        }

        return labSectionRepository.save(labSection);
    }

    @Override
    public void deleteLabSection(Long id) {
        if (!labSectionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab section not found.");
        }
        labSectionRepository.deleteById(id);
    }


    // Utility methods 

    private Long generateNextLabSectionId(Long courseId) { // Ensures unique incrementing setion ids. 
        Long maxLabSectionId = labSectionRepository.findMaxLabSectionIdForCourse(courseId);
        return (maxLabSectionId != null) ? maxLabSectionId + 1 : 1L;
    }
}
