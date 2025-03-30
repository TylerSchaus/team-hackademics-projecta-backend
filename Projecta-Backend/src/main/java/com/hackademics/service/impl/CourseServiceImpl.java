package com.hackademics.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.AdminSummaryDto;
import com.hackademics.dto.CourseDto;
import com.hackademics.dto.CourseResponseDto;
import com.hackademics.dto.CourseUpdateDto;
import com.hackademics.dto.SubjectResponseDto;
import com.hackademics.model.Course;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.CourseService;
import com.hackademics.util.TermDeterminator;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public CourseResponseDto convertToResponseDto(Course course) {
        AdminSummaryDto adminDto = new AdminSummaryDto(
            course.getAdmin().getId(),
            course.getAdmin().getFirstName(),
            course.getAdmin().getLastName(),
            course.getAdmin().getAdminId()
        );

        SubjectResponseDto subjectDto = new SubjectResponseDto(
            course.getSubject().getId(),
            course.getSubject().getSubjectName(),
            course.getSubject().getSubjectTag()
        );

        CourseResponseDto responseDto = new CourseResponseDto(
            course.getId(),
            adminDto,
            subjectDto,
            course.getCourseName(),
            course.getStartDate().toLocalDate(),
            course.getEndDate().toLocalDate(),
            course.getEnrollLimit(),
            course.getCurrentEnroll(),
            course.getCourseNumber(),
            course.getCourseTag(),
            course.getTerm(),
            course.getDays(),
            course.getStartTime(),
            course.getEndTime(),
            course.getNumLabSections()
        );
        return responseDto;
    }

    @Override
    public CourseResponseDto saveCourse(CourseDto courseDto, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create courses.");
        }
        Course newCourse = new Course(
                userRepository.findByAdminId(courseDto.getAdminId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found")),
                subjectRepository.findById(courseDto.getSubjectId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found")),
                courseDto.getCourseName(),
                courseDto.getStartDate(),
                courseDto.getEndDate(),
                courseDto.getEnrollLimit(),
                courseDto.getCourseNumber(),
                courseDto.getDays(),
                courseDto.getStartTime(),
                courseDto.getEndTime());
        return convertToResponseDto(courseRepository.save(newCourse));
    }

    @Override
    public List<CourseResponseDto> getAllActiveCourses() {
        LocalDateTime now = LocalDateTime.now();
        return courseRepository.findByStartDateBeforeAndEndDateAfter(now, now).stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponseDto> getAllUpcomingCourses() {
        LocalDateTime now = LocalDateTime.now();
        return courseRepository.findByStartDateAfter(now).stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponseDto> getAllCoursesByAdmin(UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view their courses.");
        }

        return courseRepository.findByAdminId(authenticatedUser.getAdminId()).stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponseDto> getAllCoursesBySubjectId(Long id) {
        return courseRepository.findBySubjectId(id).stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public CourseResponseDto getCourseById(Long id) {
        return courseRepository.findById(id)
            .map(this::convertToResponseDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + id));
    }

    @Override
    public CourseResponseDto updateCourse(Long id, CourseUpdateDto courseUpdateDto, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can update courses.");
        }

        return courseRepository.findById(id).map(course -> {
            if (courseUpdateDto.getAdminId() != null) {
                User newAdmin = userRepository.findById(courseUpdateDto.getAdminId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assigned Admin not found"));
                course.setAdmin(newAdmin);
            }

            if (courseUpdateDto.getCourseName() != null) {
                course.setCourseName(courseUpdateDto.getCourseName());
            }

            if (courseUpdateDto.getCourseNumber() != null) {
                course.setCourseNumber(courseUpdateDto.getCourseNumber());
            }

            if (courseUpdateDto.getSubjectId() != null) {
                Subject newSubject = subjectRepository.findById(courseUpdateDto.getSubjectId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found"));
                course.setSubject(newSubject);
            }

            if (courseUpdateDto.getEnrollLimit() != null) {
                course.setEnrollLimit(courseUpdateDto.getEnrollLimit());
            }

            if (courseUpdateDto.getStartDate() != null) {
                course.setStartDate(courseUpdateDto.getStartDate());
                course.setTerm(TermDeterminator.determineTerm(courseUpdateDto.getStartDate()));
            }

            if (courseUpdateDto.getEndDate() != null) {
                course.setEndDate(courseUpdateDto.getEndDate());
            }

            if (courseUpdateDto.getSubjectId() != null || courseUpdateDto.getCourseNumber() != null) {
                course.setCourseTag(course.getSubject().getSubjectTag() + " " + course.getCourseNumber());
            }

            if (courseUpdateDto.getDays() != null){
                course.setDays(courseUpdateDto.getDays()); 
            }

            if (courseUpdateDto.getStartTime() != null){
                course.setStartTime(courseUpdateDto.getStartTime()); 
            }
            if (courseUpdateDto.getEndTime() != null){
                course.setEndTime(courseUpdateDto.getEndTime()); 
            } 

            return convertToResponseDto(courseRepository.save(course));
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    @Override
    public void deleteCourse(Long id, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete courses.");
        }

        if (!courseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found.");
        }

        courseRepository.deleteById(id);
    }
}
