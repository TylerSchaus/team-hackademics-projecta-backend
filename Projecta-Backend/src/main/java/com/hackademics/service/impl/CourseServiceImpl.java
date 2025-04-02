package com.hackademics.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.RequestDto.CourseDto;
import com.hackademics.dto.ResponseDto.CourseResponseDto;
import com.hackademics.dto.UpdateDto.CourseUpdateDto;
import com.hackademics.model.Course;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.service.CourseService;
import com.hackademics.util.ConvertToResponseDto;
import com.hackademics.util.RoleBasedAccessVerification;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private RoleBasedAccessVerification roleBasedAccessVerification;

    @Override
    public CourseResponseDto saveCourse(CourseDto courseDto, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
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
        
        Course savedCourse = courseRepository.save(newCourse);
        Waitlist waitlist = waitlistRepository.findByCourseId(savedCourse.getId());
        return ConvertToResponseDto.convertToCourseResponseDto(savedCourse, waitlist);
    }

    @Override
    public List<CourseResponseDto> getAllActiveCourses() {
        LocalDateTime now = LocalDateTime.now();
        return courseRepository.findByStartDateBeforeAndEndDateAfter(now, now).stream()
                .map(course -> {
                    Waitlist waitlist = waitlistRepository.findByCourseId(course.getId());
                    return ConvertToResponseDto.convertToCourseResponseDto(course, waitlist);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponseDto> getAllUpcomingCourses() {
        LocalDateTime now = LocalDateTime.now();
        return courseRepository.findByStartDateAfter(now).stream()
                .map(course -> {
                    Waitlist waitlist = waitlistRepository.findByCourseId(course.getId());
                    return ConvertToResponseDto.convertToCourseResponseDto(course, waitlist);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponseDto> getAllCoursesByAdmin(UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view all courses.");
        }
        User admin = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
        return courseRepository.findByAdminId(admin.getAdminId()).stream()
                .map(course -> {
                    Waitlist waitlist = waitlistRepository.findByCourseId(course.getId());
                    return ConvertToResponseDto.convertToCourseResponseDto(course, waitlist);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponseDto> getAllCoursesBySubjectId(Long id) {
        return courseRepository.findBySubjectId(id).stream()
                .map(course -> {
                    Waitlist waitlist = waitlistRepository.findByCourseId(course.getId());
                    return ConvertToResponseDto.convertToCourseResponseDto(course, waitlist);
                })
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponseDto getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        Waitlist waitlist = waitlistRepository.findByCourseId(course.getId());
        return ConvertToResponseDto.convertToCourseResponseDto(course, waitlist);
    }

    @Override
    public CourseResponseDto updateCourse(Long id, CourseUpdateDto courseUpdateDto, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can update courses.");
        }
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        
        if (courseUpdateDto.getCourseName() != null) {
            course.setCourseName(courseUpdateDto.getCourseName());
        }
        if (courseUpdateDto.getStartDate() != null) {
            course.setStartDate(courseUpdateDto.getStartDate());
        }
        if (courseUpdateDto.getEndDate() != null) {
            course.setEndDate(courseUpdateDto.getEndDate());
        }
        if (courseUpdateDto.getEnrollLimit() != null) {
            course.setEnrollLimit(courseUpdateDto.getEnrollLimit());
        }
        if (courseUpdateDto.getCourseNumber() != null) {
            course.setCourseNumber(courseUpdateDto.getCourseNumber());
        }
        if (courseUpdateDto.getDays() != null) {
            course.setDays(courseUpdateDto.getDays());
        }
        if (courseUpdateDto.getStartTime() != null) {
            course.setStartTime(courseUpdateDto.getStartTime());
        }
        if (courseUpdateDto.getEndTime() != null) {
            course.setEndTime(courseUpdateDto.getEndTime());
        }

        Course updatedCourse = courseRepository.save(course);
        Waitlist waitlist = waitlistRepository.findByCourseId(updatedCourse.getId());
        return ConvertToResponseDto.convertToCourseResponseDto(updatedCourse, waitlist);
    }

    @Override
    public void deleteCourse(Long id, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete courses.");
        }
        if (!courseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
        courseRepository.deleteById(id);
    }
}
