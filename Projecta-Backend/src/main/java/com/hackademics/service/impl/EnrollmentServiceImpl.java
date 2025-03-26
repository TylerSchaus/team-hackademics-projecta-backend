package com.hackademics.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.CourseResponseDto;
import com.hackademics.dto.EnrollmentDto;
import com.hackademics.dto.EnrollmentResponseDto;
import com.hackademics.dto.LabSectionResponseDto;
import com.hackademics.dto.StudentSummaryDto;
import com.hackademics.model.Course;
import com.hackademics.model.Enrollment;
import com.hackademics.model.LabSection;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.EnrollmentRepository;
import com.hackademics.repository.LabSectionRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistEnrollmentRepository;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.service.CourseService;
import com.hackademics.service.EnrollmentService;
import com.hackademics.service.LabSectionService;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private LabSectionRepository labSectionRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private WaitlistEnrollmentRepository waitlistEnrollmentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private LabSectionService labSectionService;

    private EnrollmentResponseDto convertToResponseDto(Enrollment enrollment) {
        CourseResponseDto courseDto = courseService.getCourseById(enrollment.getCourse().getId());
        StudentSummaryDto studentDto = new StudentSummaryDto(
                enrollment.getStudent().getId(),
                enrollment.getStudent().getFirstName(),
                enrollment.getStudent().getLastName(),
                enrollment.getStudent().getEmail()
        );
        LabSectionResponseDto labSectionDto = enrollment.getLabSection() != null
                ? labSectionService.getLabSectionById(enrollment.getLabSection().getId()) : null;

        return new EnrollmentResponseDto(
                enrollment.getId(),
                courseDto,
                studentDto,
                labSectionDto
        );
    }

    @Override
    public EnrollmentResponseDto saveEnrollment(EnrollmentDto enrollmentDto, UserDetails currentUser) {
        // User verification
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        User student = userRepository.findById(enrollmentDto.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        if (authenticatedUser.getRole() != Role.ADMIN && !authenticatedUser.getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Students can only enroll themselves.");
        }

        // Check that the course exists
        Course course = courseRepository.findById(enrollmentDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        // Check if the student is already enrolled in the course
        List<Enrollment> currentEnrollments = enrollmentRepository.findByStudentId(student.getId());
        Enrollment existingEnrollment = currentEnrollments.stream()
                .filter(e -> e.getCourse().getId().equals(course.getId()))
                .findFirst()
                .orElse(null);

        if (existingEnrollment != null) {
            // Redirect to updateEnrollment if the student is already in the course
            return convertToResponseDto(updateEnrollment(existingEnrollment, enrollmentDto.getLabSectionId(), currentEnrollments));
        }

        // Check for schedule conflicts
        if (hasScheduleConflict(currentEnrollments, course, null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schedule conflict detected with course section.");
        }

        // Check course capacity
        if (course.getCurrentEnroll() >= course.getEnrollLimit()) {
            // Query the waitlist repository for a waitlist associated with the course
            Waitlist waitlist = waitlistRepository.findByCourseId(course.getId());
            if (waitlist != null) {
                // Proceed with waitlist logic
                List<WaitlistEnrollment> waitlistEnrollments = waitlistEnrollmentRepository.findByWaitlistId(waitlist.getId());
                if (waitlistEnrollments.size() < waitlist.getWaitlistLimit()) {
                    // Add the student to the waitlist
                    WaitlistEnrollment waitlistEnrollment = new WaitlistEnrollment(
                            waitlistEnrollments.size() + 1, // Next position in the waitlist
                            waitlist,
                            student
                    );
                    waitlistEnrollmentRepository.save(waitlistEnrollment);
                    throw new ResponseStatusException(HttpStatus.OK, "Student added to the waitlist.");
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course is at capacity and the waitlist is full.");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course is at capacity and does not have a waitlist.");
            }
        }

        // Proceed with creating the enrollment
        LabSection labSection = null;

        // Fetch lab section if provided
        if (enrollmentDto.getLabSectionId() != null) {
            labSection = labSectionRepository.findById(enrollmentDto.getLabSectionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab section not found"));

            // Check for lab section schedule conflicts
            if (hasScheduleConflict(currentEnrollments, null, labSection)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schedule conflict with lab section.");
            }

            // Check lab section capacity
            if (labSection.getCurrentEnroll() >= labSection.getCapacity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lab section is at capacity.");
            }
        } else {
            // Auto-enroll in a lab section if none is provided
            List<LabSection> labSections = labSectionRepository.findByCourseId(course.getId());
            for (LabSection section : labSections) {
                if (!hasScheduleConflict(currentEnrollments, null, section) && section.getCurrentEnroll() < section.getCapacity()) {
                    labSection = section;
                    break;
                }
            }
        }

        // Create and save the enrollment
        Enrollment enrollment = new Enrollment(course, student, labSection);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // Update course and lab section enrollment counts
        course.setCurrentEnroll(course.getCurrentEnroll() + 1);
        courseRepository.save(course);

        if (labSection != null) {
            labSection.setCurrentEnroll(labSection.getCurrentEnroll() + 1);
            labSectionRepository.save(labSection);
        }

        return convertToResponseDto(savedEnrollment);
    }

    private Enrollment updateEnrollment(Enrollment existingEnrollment, Long labSectionId, List<Enrollment> currentEnrollments) {
        LabSection newLabSection = null;

        // Fetch the new lab section if provided
        if (labSectionId != null) {
            newLabSection = labSectionRepository.findById(labSectionId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab section not found"));

            // Check for schedule conflicts
            if (hasScheduleConflict(currentEnrollments, null, newLabSection)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schedule conflict with new lab section.");
            }

            // Check lab section capacity
            if (newLabSection.getCurrentEnroll() >= newLabSection.getCapacity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New lab section is at capacity.");
            }

            // Ensure the lab section corresponds to the course
            if (!newLabSection.getCourse().getId().equals(existingEnrollment.getCourse().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lab section does not correspond to the course.");
            }
        }

        // Update the lab section in the enrollment
        LabSection oldLabSection = existingEnrollment.getLabSection();
        existingEnrollment.setLabSection(newLabSection);
        Enrollment updatedEnrollment = enrollmentRepository.save(existingEnrollment);

        // Update lab section enrollment counts
        if (oldLabSection != null) {
            oldLabSection.setCurrentEnroll(oldLabSection.getCurrentEnroll() - 1);
            labSectionRepository.save(oldLabSection);
        }

        if (newLabSection != null) {
            newLabSection.setCurrentEnroll(newLabSection.getCurrentEnroll() + 1);
            labSectionRepository.save(newLabSection);
        }

        return updatedEnrollment;
    }

    @Override
    public List<EnrollmentResponseDto> getAllEnrollments(UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view all enrollments.");
        }

        return enrollmentRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponseDto> getEnrollmentsByCourseId(Long courseId, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found.");
        }

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view enrollments for a course.");
        }

        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponseDto> getEnrollmentsByStudentId(Long studentId, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() == Role.STUDENT && !authenticatedUser.getId().equals(studentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Students can only view their own enrollments.");
        }

        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentResponseDto getEnrollmentById(Long id, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view specific enrollments.");
        }

        return convertToResponseDto(enrollment);
    }

    @Override
    public void deleteEnrollment(Long id, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

        Course course = enrollment.getCourse();

        if (authenticatedUser.getRole() != Role.ADMIN && !authenticatedUser.getId().equals(enrollment.getStudent().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Students can only delete their own enrollments.");
        }

        enrollmentRepository.deleteById(id);

        if (course.getCurrentEnroll() > 0) {
            course.setCurrentEnroll(course.getCurrentEnroll() - 1);
            courseRepository.save(course);
        }
    }

    @Override
    public List<EnrollmentResponseDto> getCurrentEnrollmentByStudentId(UserDetails currentUser, Long studentId, String term) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() == Role.STUDENT && !authenticatedUser.getId().equals(studentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Students can only view their own enrollments.");
        }

        String currentTerm = term;

        if (currentTerm == null) {

            currentTerm = switch (LocalDateTime.now().getMonth()) {
                case SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER ->
                    LocalDateTime.now().getYear() + 1 + "1";
                case JANUARY, FEBRUARY, MARCH, APRIL ->
                    LocalDateTime.now().getYear() + "2";
                default ->
                    "UNDETERMINED";
            };

        }

        if (term.charAt(5) != '1' && term.charAt(5) != '2') {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid term format. Must be in the format YY1 or YY2.");
        }

        return enrollmentRepository.findByTermAndStudentId(currentTerm, studentId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    // Helper method
    private boolean hasScheduleConflict(List<Enrollment> currentEnrollments, Course course, LabSection labSection) {
        for (Enrollment enrollment : currentEnrollments) {
            if (course != null && enrollment.getCourse().getId().equals(course.getId())) {
                return true; // Conflict with the same course
            }
            if (labSection != null && enrollment.getLabSection() != null
                    && enrollment.getLabSection().getDays() == labSection.getDays()
                    && enrollment.getLabSection().getStartTime().isBefore(labSection.getEndTime())
                    && enrollment.getLabSection().getEndTime().isAfter(labSection.getStartTime())) {
                return true; // Time conflict with lab section
            }
        }
        return false;
    }
}
